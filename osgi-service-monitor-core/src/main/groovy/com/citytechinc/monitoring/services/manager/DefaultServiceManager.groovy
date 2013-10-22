package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.services.monitor.MonitoredServiceDefinition
import com.citytechinc.monitoring.services.notification.NotificationAgentDefinition
import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.persistence.RecordPersistenceService
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(label = 'CITYTECH Service Monitor Manager Service', description = '', immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
class DefaultServiceManager implements ServiceManager {

    @Override
    void forcePoll() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<MonitoredServiceDefinition> listRegisteredMonitoredServices() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<NotificationAgentDefinition> listRegisteredNotificationAgents() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<RecordPersistenceService> listRegisteredRecordPersistenceManagers() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}
