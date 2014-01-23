package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.api.persistence.RecordPersistenceService
import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.manager.actors.MissionControlActor
import com.google.common.collect.Lists
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.scheduler.Scheduler
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 * The service manager acts as a conduit between the Felix OSGi container, its services (monitored services,
 *   notification agents, poll response handlers, and persistence services), and the actor framework. Most of this
 *   service is boiler plate code which it'd be nice to work around, but that's for a later date...
 *
 */
@Component(immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
class DefaultServiceManager implements ServiceManager {

    def missionControl

    @Reference
    Scheduler scheduler

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = MonitoredService,
            bind = 'bindMonitor',
            unbind = 'unbindMonitor')
    private List<MonitoredService> registeredMonitors = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = NotificationAgent,
            bind = 'bindNotificationAgent',
            unbind = 'unbindNotificationAgent')
    private List<NotificationAgent> registeredNotificationAgents = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = RecordPersistenceService,
            bind = 'bindPersistenceService',
            unbind = 'unbindPersistenceService')
    private List<RecordPersistenceService> registeredPersistenceServices = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = PollResponseHandler,
            bind = 'bindPollResponseHandler',
            unbind = 'unbindPollResponseHandler')
    private List<PollResponseHandler> registeredPollResponseHandlers = Lists.newCopyOnWriteArrayList()

    void bindMonitor(final MonitoredService service) {
        registeredMonitors.add(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.RegisterService(service: service)
        }
    }

    void unbindMonitor(final MonitoredService service) {

        registeredMonitors.remove(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.UnregisterService(service: service)
        }
    }

    void bindNotificationAgent(final NotificationAgent service) {

        registeredNotificationAgents.add(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.RegisterService(service: service)
        }
    }

    void unbindNotificationAgent(final NotificationAgent service) {

        registeredNotificationAgents.remove(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.UnregisterService(service: service)
        }
    }

    void bindPersistenceService(final RecordPersistenceService service) {

        registeredPersistenceServices.add(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.RegisterService(service: service)
        }
    }

    void unbindPersistenceService(final RecordPersistenceService service) {

        registeredPersistenceServices.remove(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.UnregisterService(service: service)
        }
    }

    void bindPollResponseHandler(final PollResponseHandler service) {

        registeredPollResponseHandlers.add(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.RegisterService(service: service)
        }
    }

    void unbindPollResponseHandler(final PollResponseHandler service) {

        registeredPollResponseHandlers.remove(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.UnregisterService(service: service)
        }
    }

    @Activate
    protected void activate(final Map<String, Object> properties) throws Exception {

        log.info("Starting mission control...")
        missionControl = new MissionControlActor(scheduler: scheduler)
        missionControl.start()

        log.info("Registering ${registeredMonitors.size()} monitors, " +
                "${registeredNotificationAgents.size()} notification agents, " +
                "${registeredPersistenceServices.size()} persistence handlers, and " +
                "${registeredPollResponseHandlers.size()} poll response handlers with mission control...")

        registeredMonitors.each { missionControl << new MissionControlActor.RegisterService(service: it) }
        registeredNotificationAgents.each { missionControl << new MissionControlActor.RegisterService(service: it) }
        registeredPersistenceServices.each { missionControl << new MissionControlActor.RegisterService(service: it) }
        registeredPollResponseHandlers.each { missionControl << new MissionControlActor.RegisterService(service: it) }

        log.info("Mission control started.")
    }

    @Deactivate
    protected void deactivate(final Map<String, Object> properties) throws Exception {

        if (missionControl.isActive()) {

            log.info("Unregistering ${registeredMonitors.size()} monitors, " +
                    "${registeredNotificationAgents.size()} notification agents, " +
                    "${registeredPersistenceServices.size()} persistence handlers, and " +
                    "${registeredPollResponseHandlers.size()} poll response handlers with mission control...")

            registeredMonitors.each { missionControl << new MissionControlActor.UnregisterService(service: it) }
            registeredNotificationAgents.each { missionControl << new MissionControlActor.UnregisterService(service: it) }
            registeredPersistenceServices.each { missionControl << new MissionControlActor.UnregisterService(service: it) }
            registeredPollResponseHandlers.each { missionControl << new MissionControlActor.UnregisterService(service: it) }

            log.info("Shutting down mission control...")
            missionControl.stop()
        }
    }

    @Override
    void requestAllMonitorsPoll() {

    }

    @Override
    void requestAllMonitorsPersist() {

    }

    @Override
    List<String> listMonitoredServices() {
        []
    }

    @Override
    List<String> listRecordPersistenceServices() {
        []
    }

    @Override
    List<String> listNotificationAgents() {
        []
    }

    @Override
    List<String> listPollResponseHandlers() {
        []
    }

    @Override
    List<String> listAlarmedMonitors() {
        []
    }

    @Override
    ServiceMonitorRecordHolder getRecordHolder(String identifer) {

        def id = new MissionControlActor.GetRecordHolder(identifier: identifer)
        def cameBack = missionControl.sendAndWait(id)

        log.info("id: ${id}, cameBack: ${cameBack}")

        cameBack
    }
}
