package com.citytechinc.monitoring.services.notification

import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import com.citytechinc.monitoring.services.persistence.ServiceMonitorRecord
import com.day.cq.mailer.MailService
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.OsgiUtil
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(label = 'CITYTECH Service Monitor EMail Notification', description = '', immediate = true, metatype = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@NotificationAgentDefinition()
class EmailNotification implements NotificationAgent {

    @Reference
    MailService mailService

    @Property(label = 'From email', value = [''], description = 'The from e-mail address when sending an alarm notification')
    private static final String FROM_EMAIL_PROPERTY = 'fromEmail'
    private String fromEmail

    @Property(label = 'To e-mails', value = ['', ''], description = 'List of e-mail addresses that should be notified when an alarm is triggered.')
    private static final String TO_EMAIL_ADDRESSES_PROPERTY = 'toEmailAddresses'
    private List<String> toEmailAddresses

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws Exception {

        fromEmail = OsgiUtil.toString(properties.get(FROM_EMAIL_PROPERTY), '')
        toEmailAddresses = Arrays.asList(OsgiUtil.toStringArray(properties.get(TO_EMAIL_ADDRESSES_PROPERTY)))
    }

    @Override
    void notify(List<ServiceMonitorRecordHolder> recordHolders) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
