package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.api.persistence.RecordPersistenceService
import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.manager.actors.MissionControlActor
import com.google.common.collect.Lists
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.*
import org.osgi.framework.Constants as OsgiConstants

import java.util.concurrent.atomic.AtomicBoolean

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

    def missionControl
    def ready = new AtomicBoolean(false)

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = MonitoredService, bind = 'bindMonitor', unbind = 'unbindMonitor')
    private List<MonitoredService> registeredMonitors = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = NotificationAgent, bind = 'bindNotificationAgent', unbind = 'unbindNotificationAgent')
    private List<NotificationAgent> registeredNotificationAgents = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = RecordPersistenceService, bind = 'bindPersistenceService', unbind = 'unbindPersistenceService')
    private List<RecordPersistenceService> registeredPersistenceServices = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = PollResponseHandler, bind = 'bindPollResponseHandler', unbind = 'unbindPollResponseHandler')
    private List<PollResponseHandler> registeredPollResponseHandlers = Lists.newCopyOnWriteArrayList()

    protected void bindMonitor(final MonitoredService monitoredService) {

        registeredMonitors.add(monitoredService)

        if (ready) {
            missionControl << monitoredService
        }
    }

    protected void unbindMonitor(final MonitoredService monitoredService) {

        registeredMonitors.remove(monitoredService)

        if (ready) {
            missionControl << monitoredService
        }
    }

    protected void bindNotificationAgent(final NotificationAgent notificationAgent) {

        registeredNotificationAgents.add(notificationAgent)

        if (ready) {
            missionControl << notificationAgent
        }
    }

    protected void unbindNotificationAgent(final NotificationAgent notificationAgent) {

        registeredNotificationAgents.remove(notificationAgent)

        if (ready) {
            missionControl << notificationAgent
        }
    }

    protected void bindPersistenceService(final RecordPersistenceService recordPersistenceService) {

        registeredPersistenceServices.add(recordPersistenceService)

        if (ready) {
            missionControl << recordPersistenceService
        }
    }

    protected void unbindPersistenceService(final RecordPersistenceService recordPersistenceService) {

        registeredPersistenceServices.remove(recordPersistenceService)

        if (ready) {
            missionControl << recordPersistenceService
        }
    }

    protected void bindPollResponseHandler(final PollResponseHandler pollResponseHandler) {

        registeredPollResponseHandlers.add(pollResponseHandler)

        if (ready) {
            missionControl << pollResponseHandler
        }
    }

    protected void unbindPollResponseHandler(final PollResponseHandler pollResponseHandler) {

        registeredPollResponseHandlers.remove(pollResponseHandler)

        if (ready) {
            missionControl << pollResponseHandler
        }
    }

    @Activate
    protected void activate(final Map<String, Object> properties) throws Exception {

        missionControl = new MissionControlActor()

        registeredMonitors.each { missionControl << it }
        registeredNotificationAgents.each { missionControl << it }
        registeredPersistenceServices.each { missionControl << it }
        registeredPollResponseHandlers.each { missionControl << it }

        missionControl.start()
        ready.set(true)
    }

    @Deactivate
    protected void deactivate(final Map<String, Object> properties) throws Exception {

        if (ready) {

            missionControl.terminate()
            ready.set(false)
        }
    }
}
