package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import com.citytechinc.monitoring.services.manager.actors.MonitoredServiceActor
import com.citytechinc.monitoring.services.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.monitor.PollResponse
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.services.manager.actors.NotificationAgentActor
import com.citytechinc.monitoring.services.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.services.manager.actors.PollResponseHandlerActor
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

@Slf4j
class MissionControlActor extends DynamicDispatchActor {

    final Map<String, ServiceMonitorRecordHolder> monitorHistory

    Map<MonitoredServiceWrapper, MonitoredServiceActor> monitors
    Map<NotificationAgentWrapper, NotificationAgentActor> notificationAgents
    Map<PollResponseHandler, PollResponseHandlerActor> pollResponseHandlers

    MissionControlActor(monitorHistory) {

        this.monitorHistory = monitorHistory
    }

    void onMessage(MonitoredService monitoredService) {

        def wrapper = new MonitoredServiceWrapper(monitor: monitoredService)

        if (monitors.containsValue(wrapper)) {

            log.info("Termating actor for monitor ${wrapper.monitor.class.name}")
            monitors.get(wrapper).terminate()

        } else {

            def actor = new MonitoredServiceActor(wrapper: wrapper)
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
