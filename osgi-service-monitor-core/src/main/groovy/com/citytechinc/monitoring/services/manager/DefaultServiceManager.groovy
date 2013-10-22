package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.services.monitor.MonitoredService
import com.citytechinc.monitoring.services.monitor.MonitoredServiceDefinition
import com.citytechinc.monitoring.services.monitor.state.ServiceStateMonitor
import com.citytechinc.monitoring.services.notification.NotificationAgent
import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.notification.NotificationAgentDefinition
import com.citytechinc.monitoring.services.persistence.RecordPersistenceService
import com.citytechinc.monitoring.services.persistence.RecordPersistenceServiceDefinition
import com.google.common.collect.Maps
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
class DefaultServiceManager implements ServiceManager {

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = ServiceStateMonitor, bind = "bindStateMonitors", unbind = "unbindStateMonitors")
    private Map<ServiceStateMonitor, ServiceMonitorRecordHolder> stateMonitors = Maps.newConcurrentMap()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = MonitoredService, bind = "bindMonitors", unbind = "unbindMonitors")
    private Map<MonitoredService, ServiceMonitorRecordHolder> monitors = Maps.newConcurrentMap()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = NotificationAgent, bind = "bindNotificationAgents", unbind = "unbindNotificationAgents")
    private Map<NotificationAgent, ServiceMonitorRecordHolder> notificationAgents = Maps.newConcurrentMap()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = RecordPersistenceService, bind = "bindPersistenceServices", unbind = "unbindPersistenceServices")
    private Map<RecordPersistenceService, ServiceMonitorRecordHolder> persistenceServices = Maps.newConcurrentMap()

    protected void bindStateMonitors(final ServiceStateMonitor serviceStateMonitor) {
        stateMonitors.put(serviceStateMonitor)
    }

    protected void unbindStateMonitors(final ServiceStateMonitor serviceStateMonitor) {
        stateMonitors.remove(serviceStateMonitor)
    }

    protected void bindMonitors(final MonitoredService monitoredService) {
        def definition = monitoredService.class.getAnnotation(MonitoredServiceDefinition)
        monitors.put(monitoredService, null)
    }

    protected void unbindMonitors(final MonitoredService monitoredService) {
        monitors.remove(monitoredService)
    }

    protected void bindNotificationAgents(final NotificationAgent notificationAgent) {
        def definition = notificationAgent.class.getAnnotation(NotificationAgentDefinition)
        notificationAgents.put(notificationAgent, null)
    }

    protected void unbindNotificationAgents(final NotificationAgent notificationAgent) {
        notificationAgents.remove(notificationAgent)
    }

    protected void bindPersistenceServices(final RecordPersistenceService recordPersistenceService) {
        def definition = recordPersistenceService.class.getAnnotation(RecordPersistenceServiceDefinition)
        persistenceServices.put(recordPersistenceService, null)
    }

    protected void unbindPersistenceServices(final RecordPersistenceService recordPersistenceService) {
        persistenceServices.remove(recordPersistenceService)
    }

    @Override
    void forcePoll() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
