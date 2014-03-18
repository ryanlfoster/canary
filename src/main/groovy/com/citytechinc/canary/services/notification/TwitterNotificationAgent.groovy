package com.citytechinc.canary.services.notification

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.notification.AlarmNotification
import com.citytechinc.canary.api.notification.AlarmResetNotification
import com.citytechinc.canary.api.notification.NotificationAgent
import com.citytechinc.canary.api.notification.NotificationAgentDefinition
import com.citytechinc.canary.api.notification.SubscriptionStrategy
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.PropertyOption
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Constants as OsgiConstants
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Component(policy = ConfigurationPolicy.REQUIRE, label = 'Canary Framework Twitter Notification Agent', description = '', immediate = true, metatype = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@NotificationAgentDefinition(strategy = SubscriptionStrategy.ALL)
class TwitterNotificationAgent implements NotificationAgent {

    @Property(name = 'accessToken', label = 'Twitter Access Token')
    private String accessToken

    @Property(name = 'accessSecret', label = 'Twitter Access Secret')
    private String accessSecret

    @Property(name = 'consumerKey', label = 'Twitter Consumer Key')
    private String consumerKey

    @Property(name = 'consumerSecret', label = 'Tiwtter Consumer Secret')
    private String consumerSecret

    @Property(name = 'type', label = 'Notification Action Type', options = [
            @PropertyOption(value = 'Update Status Only', name = 'UPDATE_STATUS'),
            @PropertyOption(value = 'Direct Message Users', name = 'DIRECT_MESSAGE_USERS'),
            @PropertyOption(value = 'Both', name = 'BOTH')])
    private TwitterNotificationActionType type
    enum TwitterNotificationActionType { UPDATE_STATUS, DIRECT_MESSAGE_USERS, BOTH }

    @Property(name = 'directMessageTwitterHandles', label = 'Direct Message Twitter Handles', value = ['', ''], description = 'Used if type is \'Direct Message Users\' or \'Both\', this represents a list of twitter handles that will be direct message when a notification event occurs')
    private List<String> directMessageTwitterHandles

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {

        accessToken = PropertiesUtil.toString(properties.get('accessToken'), '')
        accessSecret = PropertiesUtil.toString(properties.get('accessSecret'), '')
        consumerKey = PropertiesUtil.toString(properties.get('consumerKey'), '')
        consumerSecret = PropertiesUtil.toString(properties.get('consumerSecret'), '')
        type = TwitterNotificationActionType.valueOf(PropertiesUtil.toString(properties.get('type'), 'UPDATE_STATUS'))
        directMessageTwitterHandles = PropertiesUtil.toStringArray(properties.get('directMessageTwitterHandles')) as List
    }

    @Override
    void handleAlarm(List<AlarmNotification> alarmNotifications) {

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret)

        try {

            TwitterFactory factory = new TwitterFactory(configurationBuilder.build())
            Twitter twitter = factory.getInstance()

            twitter.updateStatus('Canary test')
        } catch (Exception e) {

            log.error("An error occurred attempting to notify twitter of an alarm", e)
        }
    }

    @Override
    void handleAlarmReset(List<AlarmResetNotification> alarmResetNotifications) {

    }
}
