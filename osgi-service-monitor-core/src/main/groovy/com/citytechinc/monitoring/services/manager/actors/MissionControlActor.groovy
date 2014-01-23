package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.persistence.RecordPersistenceService
import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor
import org.apache.sling.commons.scheduler.Scheduler

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

    static class RegisterService { def service }
    static class UnregisterService { def service }
    static class GetRecordHolder { String identifier }
    static class ClearAlarm { String identifer }
    static class ForcePoll { }

    Scheduler scheduler

    def monitors = [:]
    def notificationAgents = [:]
    def pollResponseHandlers = [:]
    def recordPersistenceServices = [:]

    void onMessage(RegisterService message) {

        log.info("Received registration message for service ${message.service}")

        if (message.service instanceof MonitoredService) {

            def wrapper = new MonitoredServiceWrapper(message.service)

            if (!monitors.containsKey(wrapper)) {

                if (recordPersistenceServices.isEmpty()) {

                    log.info("No record persistence services to poll for data, starting a clean actor...")

                    // INSTANTIATE A NEW ACTOR WITH AN EMPTY RECORD HOLDER
                    def actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this, recordHolder: ServiceMonitorRecordHolder.CREATE_NEW(wrapper))
                    actor.start()

                } else {

                    /**
                     * Sort the persistence services by ranking and get the first (highest order ranking). Use the obtained
                     *   key to get a handle on the actor instance and send a message requesting the Record data. The message
                     *   transmission is non-blocking. Its response will invoke the closure, providing the record holder from
                     *   the persistence service, and start the actor with history.
                     */
                    def persistenceWrapper = recordPersistenceServices.keySet().sort { it.definition.ranking() }.first()

                    log.info("Polling ${persistenceWrapper.service.class} for records...")

                    recordPersistenceServices.get(persistenceWrapper).sendAndContinue(new RecordPersistenceServiceActor.GetRecord(wrapper.monitorServiceClassName), { recordHolder ->

                        log.info("Received record ${recordHolder} from persistence service")

                        def actor

                        if (recordHolder.present()) {

                            log.info("Record holder is present, setting record holder on actor, starting actor...")
                            actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this, recordHolder: recordHolder.get())
                        } else {

                            log.fino("Record holder is absent, starting a clean actor...")
                            actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this, recordHolder: ServiceMonitorRecordHolder.CREATE_NEW(wrapper))
                        }

                        actor.start()
                    })
                }

                monitors.put(wrapper, actor)
            }

        } else if (message.service instanceof NotificationAgent) {

            def wrapper = new NotificationAgentWrapper(message.service)

            if (!notificationAgents.containsKey(wrapper)) {

                def actor = new NotificationAgentActor(wrapper: wrapper)
                actor.start()

                notificationAgents.put(wrapper, actor)
            }

        } else if (message.service instanceof RecordPersistenceService) {

            def wrapper = new RecordPersistenceServiceWrapper(message.service)

            if (!recordPersistenceServices.containsKey(wrapper)) {

                def actor = new RecordPersistenceServiceActor(wrapper: wrapper)
                actor.start()

                recordPersistenceServices.put(wrapper, actor)
            }

        } else if (message.service instanceof PollResponseHandler) {

            def wrapper = new PollResponseWrapper(message.service)

            if (!pollResponseHandlers.containsKey(wrapper)) {

                def actor = new PollResponseHandlerActor(wrapper: wrapper)
                actor.start()

                pollResponseHandlers.put(wrapper, actor)
            }
        }
    }

    void onMessage(UnregisterService message) {

        log.info("Received un-registration message for service ${message.service}")

        if (message.service instanceof MonitoredService) {

            def wrapper = new MonitoredServiceWrapper(message.service)

            if (monitors.containsKey(wrapper)) {

                monitors.get(wrapper).terminate()
                monitors.remove(wrapper)
            }

        } else if (message.service instanceof NotificationAgent) {

            def wrapper = new NotificationAgentWrapper(message.service)

            if (notificationAgents.containsKey(wrapper)) {

                notificationAgents.get(wrapper).terminate()
                notificationAgents.remove(wrapper)
            }

        } else if (message.service instanceof RecordPersistenceService) {

            def wrapper = new RecordPersistenceServiceWrapper(message.service)

            if (recordPersistenceServices.containsKey(wrapper)) {

                recordPersistenceServices.get(wrapper).terminate()
                recordPersistenceServices.remove(wrapper)
            }

        } else if (message.service instanceof PollResponseHandler) {

            def wrapper = new PollResponseWrapper(message.service)

            if (pollResponseHandlers.containsKey(wrapper)) {

                pollResponseHandlers.get(wrapper).terminate()
                pollResponseHandlers.remove(wrapper)
            }
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

        log.info("Sending ${message} to ${pollResponseHandlers.size()} poll response handlers")

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

        log.info("Sending ${message} to ${notificationAgents.size()} notification agents")

        notificationAgents.values().each { it << message }
    }

}
