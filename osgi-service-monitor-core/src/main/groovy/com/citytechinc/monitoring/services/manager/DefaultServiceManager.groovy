package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.api.persistence.RecordPersistenceService
import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.MissionControlActor
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.MonitoredServiceServiceRegistration
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.NotificationAgentServiceRegistration
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.PollResponseServiceRegistration
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.RecordPersistenceServiceRegistration
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.RegistrationType
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

    def missionControl = new MissionControlActor()

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

    protected void bindMonitor(final MonitoredService service) {
        registeredMonitors.add(service)

        if (missionControl.isActive()) {
            missionControl << new MonitoredServiceServiceRegistration(
                    service: service,
                    type: RegistrationType.register)
        }
    }

    protected void unbindMonitor(final MonitoredService service) {

        registeredMonitors.remove(service)

        if (missionControl.isActive()) {
            missionControl << new MonitoredServiceServiceRegistration(
                    service: service,
                    type: RegistrationType.unregister)
        }
    }

    protected void bindNotificationAgent(final NotificationAgent service) {

        registeredNotificationAgents.add(service)

        if (missionControl.isActive()) {
            missionControl << new NotificationAgentServiceRegistration(
                    service: service,
                    type: RegistrationType.register)
        }
    }

    protected void unbindNotificationAgent(final NotificationAgent service) {

        registeredNotificationAgents.remove(service)

        if (missionControl.isActive()) {
            missionControl << new NotificationAgentServiceRegistration(
                    service: service,
                    type: RegistrationType.unregister)
        }
    }

    protected void bindPersistenceService(final RecordPersistenceService service) {

        registeredPersistenceServices.add(service)

        if (missionControl.isActive()) {
            missionControl << new RecordPersistenceServiceRegistration(
                    service: service,
                    type: RegistrationType.register)
        }
    }

    protected void unbindPersistenceService(final RecordPersistenceService service) {

        registeredPersistenceServices.remove(service)

        if (missionControl.isActive()) {
            missionControl << new RecordPersistenceServiceRegistration(
                    service: service,
                    type: RegistrationType.unregister)
        }
    }

    protected void bindPollResponseHandler(final PollResponseHandler service) {

        registeredPollResponseHandlers.add(service)

        if (missionControl.isActive()) {
            missionControl << new PollResponseServiceRegistration(
                    service: service,
                    type: RegistrationType.register)
        }
    }

    protected void unbindPollResponseHandler(final PollResponseHandler service) {

        registeredPollResponseHandlers.remove(service)

        if (missionControl.isActive()) {
            missionControl << new PollResponseServiceRegistration(
                    service: service,
                    type: RegistrationType.unregister)
        }
    }

    @Activate
    protected void activate(final Map<String, Object> properties) throws Exception {

        missionControl.start()

        registeredMonitors.each { missionControl << new MonitoredServiceServiceRegistration(
                service: it,
                type: RegistrationType.register)}

        registeredNotificationAgents.each { missionControl << new NotificationAgentServiceRegistration(
                service: it,
                type: RegistrationType.register)}

        registeredPersistenceServices.each { missionControl << new RecordPersistenceServiceRegistration(
                service: it,
                type: RegistrationType.register)}

        registeredPollResponseHandlers.each { missionControl << new PollResponseServiceRegistration(
                service: it,
                type: RegistrationType.register)}
    }

    @Deactivate
    protected void deactivate(final Map<String, Object> properties) throws Exception {

        if (missionControl.isActive()) {

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
