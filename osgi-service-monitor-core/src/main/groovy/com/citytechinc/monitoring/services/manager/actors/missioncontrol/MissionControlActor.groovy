package com.citytechinc.monitoring.services.manager.actors.missioncontrol

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import com.citytechinc.monitoring.services.manager.actors.RecordPersistenceServiceActor
import com.citytechinc.monitoring.services.manager.actors.notificationagent.NotificationAgentActor
import com.citytechinc.monitoring.services.manager.actors.PollResponseHandlerActor
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.MonitoredServiceServiceRegistration
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.NotificationAgentServiceRegistration
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.PollResponseServiceRegistration
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.RecordPersistenceServiceRegistration
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.messages.RegistrationType
import com.citytechinc.monitoring.services.manager.actors.monitor.MonitoredServiceActor
import groovy.transform.Immutable
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Slf4j
class MissionControlActor extends DynamicDispatchActor {

    @Immutable
    static class GetRecordHolder { String identifier }

    @Immutable
    static class ClearAlarm { String identifer }

    @Immutable
    static class ForcePoll { }

    Map<MonitoredServiceWrapper, MonitoredServiceActor> monitors = [:]
    Map<NotificationAgentWrapper, NotificationAgentActor> notificationAgents = [:]
    Map<PollResponseWrapper, PollResponseHandlerActor> pollResponseHandlers = [:]
    Map<RecordPersistenceServiceWrapper, RecordPersistenceServiceActor> recordPersistenceServices = [:]

    def getHighestOrderPersistenceService = { def relevantRecord ->

        // singular load record
        def highestOrderService = recordPersistenceServices.keySet().sort { it.definition.ranking() }.first().service
        highestOrderService.getRecordHolder(relevantRecord)
    }

    void onMessage(RecordPersistenceServiceRegistration message) {

        def wrapper = new RecordPersistenceServiceWrapper(message.service)

        if (message.type == RegistrationType.register && !recordPersistenceServices.containsKey(wrapper)) {

            def actor = new RecordPersistenceServiceActor(wrapper: wrapper)
            actor.start()

            log.debug("Starting actor for persistence service ${wrapper.service.class.name}")
            recordPersistenceServices.add(wrapper)

        } else if (message.type == RegistrationType.unregister) {

            log.debug("Terminating actor for persistence service ${wrapper.service.class.name}")
            recordPersistenceServices.get(wrapper).terminate()
            recordPersistenceServices.remove(wrapper)
        }
    }

    void onMessage(MonitoredServiceServiceRegistration message) {

        def wrapper = new MonitoredServiceWrapper(message.service)

        if (message.type == RegistrationType.register && !monitors.containsKey(wrapper)) {

            // PULL IN FROM DB
            def actor = new MonitoredServiceActor(wrapper: wrapper, missionControl: this, recordHolder: ServiceMonitorRecordHolder.CREATE_NEW(wrapper))
            actor.start()

            log.debug("Starting actor for monitor ${wrapper.monitor.class.name}")

            monitors.put(wrapper, actor)

        } else if (message.type == RegistrationType.unregister) {

            log.debug("Termating actor for monitor ${wrapper.monitor.class.name}")
            monitors.get(wrapper).terminate()
            monitors.remove(wrapper)
        }
    }

    void onMessage(NotificationAgentServiceRegistration message) {

        def wrapper = new NotificationAgentWrapper(message.service)

        if (message.type == RegistrationType.register && !notificationAgents.containsKey(wrapper)) {

            def actor = new NotificationAgentActor(wrapper: wrapper)
            actor.start()

            log.debug("Starting actor for notification agent ${wrapper.agent.class.name}")

            notificationAgents.put(wrapper, actor)

        } else if (message.type == RegistrationType.unregister) {

            log.debug("Termating actor for notification agent ${wrapper.agent.class.name}")
            notificationAgents.get(wrapper).terminate()
            notificationAgents.remove(wrapper)
        }
    }

    void onMessage(PollResponseServiceRegistration message) {

        def wrapper = new PollResponseWrapper(message.service)

        if (message.type == RegistrationType.register && !pollResponseHandlers.containsKey(wrapper)) {

            def actor = new PollResponseHandlerActor(wrapper: wrapper)
            actor.start()

            log.debug("Starting actor for poll response handler ${message.service.class.name}")
            pollResponseHandlers.put(wrapper, actor)

        } else if (message.type == RegistrationType.unregister) {

            log.debug("Termating actor for poll response handler ${message.service.class.name}")
            pollResponseHandlers.get(wrapper, terminate())
            pollResponseHandlers.remove(wrapper)
        }
    }

    void onMessage(GetRecordHolder message) {

        log.info("Got a record request for ${message.identifier}")

        def actor = monitors.values().first()
        def records = actor.sendAndWait(new MonitoredServiceActor.GetRecords(), 1L, TimeUnit.SECONDS)

        sender.send(records)
    }

    /**
     *
     * If a MonitoredServiceActor sends a
     *
     * @param message
     */
    void onMessage(DetailedPollResponse message) {

        pollResponseHandlers.values().each { it << message }
    }

    /**
     *
     * If a MonitoredServiceActor sends an entire ServiceMonitorRecordHolder, it indicates that it is in
     *   an alarmed state and thus sends it's entire set of records.
     *
     * MissionControlActor is responsible for then notification any NotificationAgentActors with the entire
     *   set of records. MissionControlActor is also responsible for requesting persistence of the alarmed state.
     *
     * @param message
     */
    void onMessage(ServiceMonitorRecordHolder message) {

        notificationAgents.values().each { it << message }
    }

}
