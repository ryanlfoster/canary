package com.citytechinc.canary.services.manager

import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.notification.NotificationAgent
import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.persistence.RecordPersistenceService
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.canary.api.responsehandler.PollResponseHandler
import com.citytechinc.canary.api.responsehandler.PollResponseWrapper
import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.RecordHolder
import com.citytechinc.canary.services.manager.actors.MissionControlActor
import com.citytechinc.canary.services.manager.actors.Statistics
import com.citytechinc.canary.services.manager.actors.monitor.MonitoredServiceActor
import com.google.common.base.Optional
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
import org.apache.sling.commons.osgi.PropertiesUtil
import org.apache.sling.commons.scheduler.Scheduler
import org.osgi.framework.Constants as OsgiConstants

import java.util.concurrent.TimeUnit

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

    MissionControlActor missionControl

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

    @Property(label = "Mission Control Monitor Actor Timeout", longValue = 15L, description = "The number of seconds the activate block of this service will sleep before issuing a message to the Mission Control actor informing it to instantiate monitor actors.")
    private static final String MISSION_CONTROL_MONITOR_ACTOR_TIMEOUT = 'missionControlMonitorActorTimeout'
    private Long missionControlMonitorActorTimeout;

    void bindMonitor(final MonitoredService service) {
        registeredMonitors.add(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.ServiceLifecycleEvent(service: service, isRegistration: true)
        }
    }

    void unbindMonitor(final MonitoredService service) {

        registeredMonitors.remove(service)

        sleep(100)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.ServiceLifecycleEvent(service: service, isRegistration: false)
        }
    }

    void bindNotificationAgent(final NotificationAgent service) {

        registeredNotificationAgents.add(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.ServiceLifecycleEvent(service: service, isRegistration: true)
        }
    }

    void unbindNotificationAgent(final NotificationAgent service) {

        registeredNotificationAgents.remove(service)

        sleep(100)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.ServiceLifecycleEvent(service: service, isRegistration: false)
        }
    }

    void bindPersistenceService(final RecordPersistenceService service) {

        registeredPersistenceServices.add(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.ServiceLifecycleEvent(service: service, isRegistration: true)
        }
    }

    void unbindPersistenceService(final RecordPersistenceService service) {

        registeredPersistenceServices.remove(service)

        sleep(100)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.ServiceLifecycleEvent(service: service, isRegistration: false)
        }
    }

    void bindPollResponseHandler(final PollResponseHandler service) {

        registeredPollResponseHandlers.add(service)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.ServiceLifecycleEvent(service: service, isRegistration: true)
        }
    }

    void unbindPollResponseHandler(final PollResponseHandler service) {

        registeredPollResponseHandlers.remove(service)

        sleep(100)

        if (missionControl?.isActive()) {
            missionControl << new MissionControlActor.ServiceLifecycleEvent(service: service, isRegistration: false)
        }
    }

    @Activate
    protected void activate(final Map<String, Object> properties) throws Exception {

        missionControlMonitorActorTimeout = TimeUnit.MILLISECONDS.convert(PropertiesUtil.toLong(properties.get(MISSION_CONTROL_MONITOR_ACTOR_TIMEOUT), 15), TimeUnit.SECONDS)

        log.debug('Starting mission control...')
        missionControl = new MissionControlActor(scheduler: scheduler)
        missionControl.start()

        log.debug("Sleeping for ${missionControlMonitorActorTimeout} seconds...")
        sleep(missionControlMonitorActorTimeout)

        log.info("Registering ${registeredMonitors.size()} monitors, " +
                "${registeredNotificationAgents.size()} notification agents, " +
                "${registeredPersistenceServices.size()} persistence handlers, and " +
                "${registeredPollResponseHandlers.size()} poll response handlers with mission control...")

        registeredPersistenceServices.each { missionControl << new MissionControlActor.ServiceLifecycleEvent(service: it, isRegistration: true) }
        registeredMonitors.each { missionControl << new MissionControlActor.ServiceLifecycleEvent(service: it, isRegistration: true) }
        registeredNotificationAgents.each { missionControl << new MissionControlActor.ServiceLifecycleEvent(service: it, isRegistration: true) }
        registeredPollResponseHandlers.each { missionControl << new MissionControlActor.ServiceLifecycleEvent(service: it, isRegistration: true) }

        log.debug('Sending instantiate monitors...')
        missionControl << new MissionControlActor.InstantiateMonitors()

        log.debug('Mission control started')
    }

    @Deactivate
    protected void deactivate(final Map<String, Object> properties) throws Exception {

        if (missionControl.isActive()) {

            log.debug("Unregistering ${registeredMonitors.size()} monitors, " +
                    "${registeredNotificationAgents.size()} notification agents, " +
                    "${registeredPersistenceServices.size()} persistence handlers, and " +
                    "${registeredPollResponseHandlers.size()} poll response handlers with mission control...")

            registeredMonitors.each { missionControl << new MissionControlActor.ServiceLifecycleEvent(service: it, isRegistration: false) }
            registeredNotificationAgents.each { missionControl << new MissionControlActor.ServiceLifecycleEvent(service: it, isRegistration: false) }
            registeredPersistenceServices.each { missionControl << new MissionControlActor.ServiceLifecycleEvent(service: it, isRegistration: false) }
            registeredPollResponseHandlers.each { missionControl << new MissionControlActor.ServiceLifecycleEvent(service: it, isRegistration: false) }

            log.debug("Shutting down mission control...")
            missionControl.stop()
        }
    }

    @Override
    void requestAllMonitorsPoll() {

        if (missionControl?.isActive()) {

            missionControl << new MissionControlActor.RequestAllMonitorsPoll()
        }
    }

    @Override
    void requestAllMonitorsPersist() {
        missionControl << new MissionControlActor.RequestAllMonitorsPersist()
    }

    @Override
    List<MonitoredServiceWrapper> getMonitoredServices() {
        registeredMonitors.collect { new MonitoredServiceWrapper(it) }
    }

    @Override
    List<NotificationAgentWrapper> getNotificationAgents() {
        registeredNotificationAgents.collect { new NotificationAgentWrapper(it) }
    }

    @Override
    List<PollResponseWrapper> getPollResponseHandlers() {
        registeredPollResponseHandlers.collect { new PollResponseWrapper(it) }
    }

    @Override
    List<RecordPersistenceServiceWrapper> getRecordPersistenceServices() {
        registeredPersistenceServices.collect { new RecordPersistenceServiceWrapper(it) }
    }

    @Override
    Optional<Statistics> getStatistics(String identifier, MissionControlActor.GetStatistics.Type type) {

        def message = new MissionControlActor.GetStatistics(identifier: identifier, type: type)
        missionControl.sendAndWait(message)
    }

    @Override
    Optional<RecordHolder> getRecordHolder(String identifier) {

        def message = new MissionControlActor.GetRecords(identifier: identifier)
        missionControl.sendAndWait(message)
    }

    @Override
    void resetAlarm(String identifier) {

        if (missionControl?.isActive()) {

            missionControl << new MissionControlActor.RequestMonitorResetIfAlarmed(identifier: identifier)
        }
    }

    @Override
    void resetAllAlarms() {

        if (missionControl?.isActive()) {

            missionControl << new MissionControlActor.RequestAllMonitorsResetIfAlarmed()
        }
    }
}
