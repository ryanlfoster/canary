package com.citytechinc.canary.services.notification

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.notification.AlarmNotification
import com.citytechinc.canary.api.notification.AlarmResetNotification
import com.citytechinc.canary.api.notification.NotificationAgent
import com.citytechinc.canary.api.notification.NotificationAgentDefinition
import com.citytechinc.canary.api.notification.SubscriptionStrategy
import groovy.util.logging.Slf4j
import groovyjarjarcommonscli.MissingArgumentException
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Constants as OsgiConstants

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
class TwilioSMSNotificationAgent implements NotificationAgent {

    @Property(name = 'twilioHost', label = 'Twilio Host', value = 'api.twilio.com')
    private String twilioHost

    @Property(name = 'apiID', label = 'Twilio API ID')
    private String apiID

    @Property(name = 'apiPass', label = 'Twilio API Pass')
    private String apiPass

    @Property(name = 'fromNumber', label = 'SMS From Number')
    private String fromNumber

    @Property(name = 'destinationNumbers', label = 'SMS Destination Numbers', value = ['', ''])
    private List<String> destinationNumbers

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws Exception {

        twilioHost = PropertiesUtil.toString(properties.get('twilioHost'), 'api.twilio.com')
        apiID = PropertiesUtil.toString(properties.get('apiID'), '')
        apiPass = PropertiesUtil.toString(properties.get('apiPass'), '')
        fromNumber = PropertiesUtil.toString(properties.get('fromNumber'), '')
        destinationNumbers = PropertiesUtil.toStringArray(properties.get('destinationNumbers')) as List

        if (!twilioHost && !apiID && !apiPass && !fromNumber && !destinationNumbers) {
            throw new MissingArgumentException("One or more required configuration values is not set:" +
                    " twilioHost: ${twilioHost}, apiID: ${apiID}, apiPass: ${apiPass}, fromNumber: ${fromNumber}," +
                    " destinationNumbers: ${destinationNumbers}")
        }
    }

    @Override
    void handleAlarm(List<AlarmNotification> alarmNotifications) {
//
//        HostConfiguration hc = new HostConfiguration()
//        hc.setHost(twilioHost, 443, "https")
//
//        String url = "/2008-08-01/Accounts/$apiID/SMS/Messages"
//
//        HttpClient client = new HttpClient()
//        Credentials credentials = new UsernamePasswordCredentials(apiID, apiPass)
//        client.getState().setCredentials(null, null, credentials)
//
//        destinationNumbers.each { toNumber ->
//
//            PostMethod post = new PostMethod(url)
//            post.addParameter 'IfMachine', 'Continue'
//            post.addParameter 'Method', 'POST'
//            post.addParameter 'From', fromNumber
//            post.addParameter 'To', toNumber
//            post.addParameter 'Body', "Monitors: ${alarmNotifications}*.monitorName are alarmed"
//
//            client.executeMethod(hc, post)
//
//            log.info("Received status code ${post.statusCode} and text ${post.statusText}")
//            log.trace("Received response body ${post.responseBodyAsString}")
//
//            post.releaseConnection()
//        }
    }

    @Override
    void handleAlarmReset(List<AlarmResetNotification> alarmResetNotifications) {

    }
}
