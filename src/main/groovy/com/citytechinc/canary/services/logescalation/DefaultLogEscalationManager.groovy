package com.citytechinc.canary.services.logescalation

import com.citytechinc.canary.Constants
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants
import org.osgi.service.cm.Configuration
import org.osgi.service.cm.ConfigurationAdmin

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
class DefaultLogEscalationManager implements LogEscalationManager {

    @Reference
    ConfigurationAdmin configurationAdmin

    @Override
    String escalate(String servicename) {

        Configuration newConfiguration = configurationAdmin.createFactoryConfiguration(Constants.LOG_ESCALATION_TARGET_FACTORY_PID, null)

        Dictionary<String, Object> properties = new Hashtable<String, Object>()

        properties.put('pid', servicename)
        properties.put('canary.created.date', Constants.JCR_POLL_RESPONSE_NODE_STORAGE_FORMATTER.format(new Date()))
        properties.put(Constants.LOG_LOGGERS, [servicename])
        properties.put(Constants.LOG_FILE, Constants.LOG_ESCALATION_ROOT_LOGGER_PATH + servicename)
        properties.put(Constants.LOG_LEVEL, 'debug')

        log.info("Creating configuration ${newConfiguration.pid} for service ${properties.get('pid')}")

        newConfiguration.update(properties)

        Constants.LOG_ESCALATION_ROOT_LOGGER_PATH + servicename
    }

    @Override
    void deescalate(String servicename) {

        configurationAdmin.listConfigurations().findAll { it.factoryPid == Constants.LOG_ESCALATION_TARGET_FACTORY_PID }
            .findAll { it.properties.get('canary.created.date') && it.properties.get('pid') == servicename }
            .each {

                log.info("Deleting configuration ${it.pid} for service ${it.properties.get('pid')}")
                it.delete()
            }
    }
}
