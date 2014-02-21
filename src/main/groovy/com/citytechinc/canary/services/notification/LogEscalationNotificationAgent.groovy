package com.citytechinc.canary.services.notification

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.RecordHolder
import com.citytechinc.canary.api.notification.NotificationAgent
import com.citytechinc.canary.api.notification.NotificationAgentDefinition
import com.citytechinc.canary.api.notification.SubscriptionStrategy
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants
import org.osgi.service.cm.Configuration
import org.osgi.service.cm.ConfigurationAdmin

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(policy = ConfigurationPolicy.REQUIRE, label = 'CITYTECH Log Escalation Notification Agent', description = 'Creates log entries when it receives an alarm if the service is configured to do so.', immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@NotificationAgentDefinition(strategy = SubscriptionStrategy.ALL)
class LogEscalationNotificationAgent implements NotificationAgent {

    public static final String ROOT_LOGGER_PATH = 'canary/'

    public static final String LOG_FACTORY_PID = 'org.apache.sling.commons.log.LogManager.factory.config'
    public static final String LOG_LOGGERS = 'org.apache.sling.commons.log.names'
    public static final String LOG_FILE = 'org.apache.sling.commons.log.file'
    public static final String LOG_LEVEL = 'org.apache.sling.commons.log.level'

    @Reference
    ConfigurationAdmin configurationAdmin

    @Override
    void notify(List<RecordHolder> recordHolders) {

        recordHolders.each {

            Configuration newConfiguration = configurationAdmin.createFactoryConfiguration(LOG_FACTORY_PID, null)

            Dictionary<String, Object> properties = new Hashtable<String, Object>()

            properties.put('pid', it.monitorIdentifier)
            properties.put('canary.created.date', Constants.JCR_POLL_RESPONSE_NODE_STORAGE_FORMATTER.format(new Date()))
            properties.put(LOG_LOGGERS, [it.monitorIdentifier])
            properties.put(LOG_FILE, ROOT_LOGGER_PATH + it.monitorIdentifier)
            properties.put(LOG_LEVEL, 'debug')

            log.info("Creating configuration ${newConfiguration.pid} for service ${properties.get('pid')}")

            newConfiguration.update(properties)
        }
    }
}
