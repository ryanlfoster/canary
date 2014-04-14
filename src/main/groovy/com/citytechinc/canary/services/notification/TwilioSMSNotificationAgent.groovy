package com.citytechinc.canary.services.notification

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.notification.AggregateAlarms
import com.citytechinc.canary.api.notification.AlarmNotification
import com.citytechinc.canary.api.notification.AlarmResetNotification
import com.citytechinc.canary.api.notification.NotificationAgent
import com.citytechinc.canary.api.notification.NotificationAgentDefinition
import com.citytechinc.canary.api.notification.SubscriptionStrategy
import com.google.common.base.Preconditions
import groovy.util.logging.Slf4j
import groovyjarjarcommonscli.MissingArgumentException
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Constants as OsgiConstants

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Component(policy = ConfigurationPolicy.REQUIRE, label = 'Canary Framework Twilio SMS Notification Agent', description = '', immediate = true, metatype = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@NotificationAgentDefinition(strategy = SubscriptionStrategy.ALL)
@AggregateAlarms(aggregationWindow = 1, aggregationWindowTimeUnit = TimeUnit.MINUTES)
class TwilioSMSNotificationAgent implements NotificationAgent {

    static final String HOST = 'api.twilio.com'

    @Property(name = 'accountSID', label = 'Twilio API AccountSID')
    private String accountSID

    @Property(name = 'authToken', label = 'Twilio API Auth Token')
    private String authToken

    @Property(name = 'fromNumber', label = 'SMS From Number')
    private String fromNumber

    @Property(name = 'destinationNumbers', label = 'SMS Destination Numbers', value = ['', ''])
    private List<String> destinationNumbers

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws Exception {

        accountSID = PropertiesUtil.toString(properties.get('accountSID'), '')
        authToken = PropertiesUtil.toString(properties.get('authToken'), '')
        fromNumber = PropertiesUtil.toString(properties.get('fromNumber'), '')
        destinationNumbers = PropertiesUtil.toStringArray(properties.get('destinationNumbers')) as List

        if (!accountSID && !authToken && !fromNumber && !destinationNumbers) {
            throw new MissingArgumentException("One or more required configuration values is not set:" +
                    " accountSID: ${accountSID}, authToken: ${authToken}, fromNumber: ${fromNumber}," +
                    " destinationNumbers: ${destinationNumbers}")
        }
    }

    @Override
    void handleAlarmNotification(List<AlarmNotification> alarmNotifications) {

        destinationNumbers.each {

            sendSMS("${alarmNotifications.size()} alarm(s) raised on AEM instance w/ runmodes ${alarmNotifications.collect { it.context }.collect { it.hostname }.unique()}", it)
        }
    }

    @Override
    void handleAlarmResetNotification(List<AlarmResetNotification> alarmResetNotifications) {

        destinationNumbers.each {

            sendSMS("${alarmResetNotifications.size()} alarm(s) reset on AEM instance w/ runmodes ${alarmResetNotifications.collect { it.context }.collect { it.hostname }.unique()}", it)
        }
    }

    private void sendSMS(String message, String destination) {

        DefaultHttpClient httpClient = new DefaultHttpClient()

        try {

            httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials(accountSID, authToken))

            HttpPost postMethod = new HttpPost("https://${HOST}/2010-04-01/Accounts/${accountSID}/Messages.json")

            List<NameValuePair> parameters = new ArrayList<NameValuePair>()
            parameters.add(new BasicNameValuePair('From', fromNumber))
            parameters.add(new BasicNameValuePair('To', destination))
            parameters.add(new BasicNameValuePair('Body', message))

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, 'UTF-8')

            postMethod.setEntity(entity)

            HttpResponse response = httpClient.execute(postMethod)

            log.info("Received status code ${response.statusLine.statusCode} and phrase ${response.statusLine.reasonPhrase}")
            log.trace("Received response ${EntityUtils.toString(response.entity)}")

        } finally {

            httpClient.getConnectionManager().shutdown()
        }
    }
}
