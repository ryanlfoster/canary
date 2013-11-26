package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.persistence.RecordPersistenceService
import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.monitor.PollResponse
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.services.manager.actors.monitor.MonitoredServiceActor
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

@Slf4j
class MissionControlActor extends DynamicDispatchActor {

    Map<MonitoredServiceWrapper, MonitoredServiceActor> monitors
    Map<NotificationAgentWrapper, NotificationAgentActor> notificationAgents
    Map<PollResponseHandler, PollResponseHandlerActor> pollResponseHandlers
    List<RecordPersistenceServiceWrapper> recordPersistenceServices

    def getHighestOrderPersistenceService = { def relevantRecord ->

        // singular load record
        def highestOrderService = recordPersistenceServices.sort { it.definition.ranking() }.first().service
        highestOrderService.getRecordHolder(relevantRecord)
    }

    void onMessage(RecordPersistenceService recordPersistenceService) {

        def wrapper = new RecordPersistenceServiceWrapper(service: recordPersistenceService)

        if (recordPersistenceServices.contains(wrapper)) {

            log.info("Registering persistence service ${wrapper.service.class.name}")
            recordPersistenceServices.add(wrapper)

        } else {

            log.info("Unregistering persistence service ${wrapper.service.class.name}")
            recordPersistenceServices.remove(wrapper)
        }
    }

    void onMessage(MonitoredService monitoredService) {

        def wrapper = new MonitoredServiceWrapper(monitor: monitoredService)

        if (monitors.containsValue(wrapper)) {

            log.info("Termating actor for monitor ${wrapper.monitor.class.name}")
            monitors.get(wrapper).terminate()

        } else {

            def actor = new MonitoredServiceActor(wrapper: wrapper, missionControl: this, recordHolder: null)
            actor.start()

            log.info("Starting actor for monitor ${wrapper.monitor.class.name}")

            monitors.put(new MonitoredServiceWrapper(monitoredService), actor)
        }
    }

    void onMessage(NotificationAgent notificationAgent) {

        def wrapper = NotificationAgentWrapper(notificationAgent)

        if (notificationAgents.containsKey(wrapper)) {

            log.info("Termating actor for notification agent ${wrapper.monitor.class.name}")
            notificationAgents.get(wrapper).terminate()

        } else {

            def actor = new NotificationAgentActor(wrapper: wrapper)
            actor.start()

            log.info("Starting actor for notification agent ${wrapper.monitor.class.name}")

            notificationAgents.put(new MonitoredServiceWrapper(notificationAgent), actor)
        }
    }

    void onMessage(PollResponseHandler pollResponseHandler) {

        if (pollResponseHandlers.containsKey(pollResponseHandler)) {

            log.info("Termating actor for poll response handler ${pollResponseHandler.class.name}")
            pollResponseHandlers.get(pollResponseHandler).terminate()

        } else {

            def actor = new PollResponseHandlerActor()
            actor.start()

            log.info("Starting actor for poll response handler ${pollResponseHandler.class.name}")
            pollResponseHandlers.put(pollResponseHandler, actor)

        }
    }

    void onMessage(PollResponse message) {

        pollResponseHandlers.values().each { it << message }
    }
}
