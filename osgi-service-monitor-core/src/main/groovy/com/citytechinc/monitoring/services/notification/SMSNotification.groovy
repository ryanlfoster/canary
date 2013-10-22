package com.citytechinc.monitoring.services.notification

import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.persistence.ServiceMonitorRecord
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(label = 'CITYTECH Service Monitor SMS Notification', description = '', immediate = true, metatype = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@NotificationAgentDefinition(aggregateAlarms = false)
class SMSNotification implements NotificationAgent {

    @Override
    void notify(ServiceMonitorRecord record) {

        log.info("Received notification for record: ${record}")
    }
}
