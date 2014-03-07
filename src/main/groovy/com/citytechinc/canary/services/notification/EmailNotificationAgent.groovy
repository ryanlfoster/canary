package com.citytechinc.canary.services.notification

import com.citytechinc.canary.api.notification.AlarmNotification
import com.citytechinc.canary.api.notification.AlarmResetNotification
import com.citytechinc.canary.api.notification.NotificationAgent
import com.citytechinc.canary.api.notification.NotificationAgentDefinition
import com.citytechinc.canary.api.notification.SubscriptionStrategy
import com.citytechinc.canary.Constants
import com.day.cq.mailer.MailService
import groovy.util.logging.Slf4j
import org.apache.commons.mail.HtmlEmail

import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.apache.sling.settings.SlingSettingsService
import org.osgi.framework.Constants as OsgiConstants

import javax.mail.internet.InternetAddress

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(policy = ConfigurationPolicy.REQUIRE, label = 'Canary Framework Email Notification Agent', description = 'Requires a valid MailService configuration', immediate = true, metatype = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@NotificationAgentDefinition(strategy = SubscriptionStrategy.ALL)
class EmailNotificationAgent implements NotificationAgent {

    @Reference
    MailService mailService

    @Reference
    SlingSettingsService slingSettingsService

    @Property(label = 'From email', value = [''], description = 'The from e-mail address when sending an alarm notification')
    private static final String FROM_EMAIL_PROPERTY = 'fromEmail'
    private String fromEmail

    @Property(label = 'To e-mails', value = ['', ''], description = 'List of e-mail addresses that should be notified when an alarm is triggered.')
    private static final String TO_EMAIL_ADDRESSES_PROPERTY = 'toEmailAddresses'
    private List<String> toEmailAddresses

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws Exception {

        fromEmail = PropertiesUtil.toString(properties.get(FROM_EMAIL_PROPERTY), '')
        toEmailAddresses = PropertiesUtil.toStringArray(properties.get(TO_EMAIL_ADDRESSES_PROPERTY)) as List
    }

    @Override
    void handleAlarm(List<AlarmNotification> alarmNotifications) {

        slingSettingsService.getSlingHomePath()

        if (fromEmail && !toEmailAddresses.empty) {

            HtmlEmail email = new HtmlEmail()

            email.setFrom(fromEmail)
            email.setTo(toEmailAddresses.collect { new InternetAddress(it) })
            email.setSubject('')
            email.setMsg('')
//            email.attach(new ByteArrayDataSource(
//                    MarhsallUtil.marshallToJSON(trafficControllerService.getTrafficControlDefinition()).getBytes("UTF-8"), "application/json"),
//                    String.format(ATTACHMENT_FILENAME, Constants.DATE_ONLY_WITH_DASHES_FORMATTER.format(processTime)),
//                    '',
//                    Part.ATTACHMENT)

            mailService.send(email);
        }
    }

    @Override
    void handleAlarmReset(List<AlarmResetNotification> alarmResetNotifications) {

    }
}
