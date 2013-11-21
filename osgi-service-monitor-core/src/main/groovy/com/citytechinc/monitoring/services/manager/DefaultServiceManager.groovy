package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.monitor.MonitoredService
import com.citytechinc.monitoring.services.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.services.notification.NotificationAgent
import com.citytechinc.monitoring.services.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.services.persistence.RecordPersistenceService
import com.citytechinc.monitoring.services.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.services.responsehandler.PollResponseHandler
import com.google.common.collect.Lists
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
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

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = MonitoredService, bind = "bindMonitor", unbind = "unbindMonitor")
    private List<MonitoredServiceWrapper> registeredMonitors = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = NotificationAgent, bind = "bindNotificationAgent", unbind = "unbindNotificationAgent")
    private List<NotificationAgentWrapper> registeredNotificationAgents = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = PollResponseHandler, bind = "bindPollResponseHandler", unbind = "unbindPollResponseHandler")
    private List<PollResponseHandler> registeredPollResponseHandlers = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = RecordPersistenceService, bind = "bindPersistenceService", unbind = "unbindPersistenceService")
    private List<RecordPersistenceServiceWrapper> registeredPersistenceServices = Lists.newCopyOnWriteArrayList()

    protected void bindMonitor(final MonitoredService monitoredService) {
        registeredMonitors.add(new MonitoredServiceWrapper(monitoredService))
    }

    protected void unbindMonitor(final MonitoredService monitoredService) {
    }

    protected void bindNotificationAgent(final NotificationAgent notificationAgent) {
        registeredNotificationAgents.add(new NotificationAgentWrapper(notificationAgent))
    }

    protected void unbindNotificationAgent(final NotificationAgent notificationAgent) {
    }

    protected void bindPersistenceService(final RecordPersistenceService recordPersistenceService) {
        registeredPersistenceServices.add(new RecordPersistenceServiceWrapper(recordPersistenceService))
    }

    protected void unbindPersistenceService(final RecordPersistenceService recordPersistenceService) {
    }

    protected void bindPollResponseHandler(final PollResponseHandler pollResponseHandler) {
        registeredPollResponseHandlers.add(pollResponseHandler)
    }

    protected void unbindPollResponseHandler(final PollResponseHandler pollResponseHandler) {
    }

    @Activate
    protected void activate(final Map<String, Object> properties) throws Exception {

    }

    @Deactivate
    protected void deactivate(final Map<String, Object> properties) throws Exception {

    }
}
