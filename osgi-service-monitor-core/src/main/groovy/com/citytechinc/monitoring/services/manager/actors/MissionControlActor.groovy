package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.persistence.RecordPersistenceService
import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import com.citytechinc.monitoring.services.manager.actors.monitor.MonitoredServiceActor
import com.citytechinc.monitoring.services.manager.actors.monitor.Statistics
import com.google.common.base.Optional
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
final class MissionControlActor extends DynamicDispatchActor {

    // MESSAGES
    static class RegisterService { def service }
    static class UnregisterService { def service }
    static class GetRecords { String canonicalMonitorName }
    static class ResetAlarm { String canonicalMonitorName }
    static class PersistRecords { }
    static class InstantiateMonitors { }

    Scheduler scheduler

    // WRAPPERS AND ACTORS
    final Map<MonitoredServiceWrapper, MonitoredServiceActor> monitors = [:]
    final Map<NotificationAgentWrapper, NotificationAgentActor> notificationAgents = [:]
    final Map<PollResponseWrapper, PollResponseHandlerActor> pollResponseHandlers = [:]
    final Map<RecordPersistenceServiceWrapper, RecordPersistenceServiceActor> recordPersistenceServices = [:]

    Boolean hasPassedMonitorActorInstantiationTimeout = false

    void onMessage(PersistRecords message) {

        log.info("Received a PersistRecords request. Sending record requests to all monitors and issuing callback to write to all persistence services...")

        monitors.values().each { it.sendAndContinue(new MonitoredServiceActor.GetRecords(), { RecordHolder recordHolder ->

            recordPersistenceServices.values().each {
                it << new RecordPersistenceServiceActor.PersistRecord(recordHolder: recordHolder)
            }
        })}
    }

    void onMessage(InstantiateMonitors message) {

        log.info("Starting ${monitors.size()} monitors...")

        hasPassedMonitorActorInstantiationTimeout = true

        monitors.keySet().each { MonitoredServiceWrapper wrapper ->

            instantiateMonitoredServiceActor(wrapper)
        }
    }

    void onMessage(RegisterService message) {

        log.info("Received registration message for service ${message.service}")

        if (message.service instanceof MonitoredService) {

            final MonitoredServiceWrapper wrapper = new MonitoredServiceWrapper(message.service)

            if (!monitors.containsKey(wrapper)) {

                if (hasPassedMonitorActorInstantiationTimeout) {
                    instantiateMonitoredServiceActor(wrapper)
                } else {
                    monitors.put(wrapper, null)
                }

            } else {

                log.warn("Monitored service ${message.service} already registered")
            }

        } else if (message.service instanceof NotificationAgent) {

            final NotificationAgentWrapper wrapper = new NotificationAgentWrapper(message.service)

            if (!notificationAgents.containsKey(wrapper)) {

                NotificationAgentActor actor = new NotificationAgentActor(wrapper: wrapper, scheduler: scheduler)
                actor.start()

                notificationAgents.put(wrapper, actor)
            } else {

                log.warn("Notification agent ${message.service} already registered")
            }

        } else if (message.service instanceof RecordPersistenceService) {

            final RecordPersistenceServiceWrapper wrapper = new RecordPersistenceServiceWrapper(message.service)

            if (!recordPersistenceServices.containsKey(wrapper)) {

                RecordPersistenceServiceActor actor = new RecordPersistenceServiceActor(wrapper: wrapper)
                actor.start()

                recordPersistenceServices.put(wrapper, actor)
            } else {

                log.warn("Record persistence service ${message.service} already registered")
            }

        } else if (message.service instanceof PollResponseHandler) {

            final PollResponseWrapper wrapper = new PollResponseWrapper(message.service)

            if (!pollResponseHandlers.containsKey(wrapper)) {

                PollResponseHandlerActor actor = new PollResponseHandlerActor(wrapper: wrapper)
                actor.start()

                pollResponseHandlers.put(wrapper, actor)
            } else {

                log.warn("Poll response handler ${message.service} already registered")
            }
        }
    }

    void onMessage(UnregisterService message) {

        log.info("Received un-registration message for service ${message.service}")

        if (message.service instanceof MonitoredService) {

            final MonitoredServiceWrapper wrapper = new MonitoredServiceWrapper(message.service)

            if (monitors.containsKey(wrapper)) {

                if (monitors.get(wrapper)) {

                    log.info("Terminating actor for monitored service ${message.service}")
                    monitors.get(wrapper)?.terminate()

                } else {

                    log.info("Removing non-started actor for monitored service ${message.service}")
                }

                monitors.remove(wrapper)

            } else {

                log.warn("Monitored service ${message.service} is not registered")
            }

        } else if (message.service instanceof NotificationAgent) {

            final NotificationAgentWrapper wrapper = new NotificationAgentWrapper(message.service)

            if (notificationAgents.containsKey(wrapper)) {

                log.info("Terminating actor for notification agent ${message.service}")

                notificationAgents.get(wrapper).terminate()
                notificationAgents.remove(wrapper)
            } else {

                log.warn("Notification agent ${message.service} is not registered")
            }

        } else if (message.service instanceof RecordPersistenceService) {

            final RecordPersistenceServiceWrapper wrapper = new RecordPersistenceServiceWrapper(message.service)

            if (recordPersistenceServices.containsKey(wrapper)) {

                log.info("Terminating actor for record persistence service ${message.service}")

                recordPersistenceServices.get(wrapper).terminate()
                recordPersistenceServices.remove(wrapper)
            } else {

                log.warn("Record persistence service ${message.service} is not registered")
            }

        } else if (message.service instanceof PollResponseHandler) {

            final PollResponseWrapper wrapper = new PollResponseWrapper(message.service)

            if (pollResponseHandlers.containsKey(wrapper)) {

                log.info("Terminating actor for poll response handler ${message.service}")

                pollResponseHandlers.get(wrapper).terminate()
                pollResponseHandlers.remove(wrapper)
            } else {

                log.warn("Poll response handler ${message.service} is not registered")
            }
        }
    }

    /**
     *
     * A poll request coming from the outside world. We'll push this poll request to all
     *   the canonicalMonitorName actors.
     *
     * @param message
     */
    void onMessage(MonitoredServiceActor.Poll message) {

        log.info("Sending poll requests to all ${monitors.size()} monitor actors...")

        monitors.values().each { it << message }
    }

    /**
     *
     * A record retrieval request from the outside world. We'll find the corresponding canonicalMonitorName actor
     *   and request that it send us its records.
     *
     * This call is blocking with a max time of 1 second.
     *
     * @param message
     */
    void onMessage(GetRecords message) {

        log.info("Got a record request for ${message.canonicalMonitorName}")

        def key = monitors.keySet().find { it.canonicalMonitorName == message.canonicalMonitorName }
        def records = monitors.get(key).sendAndWait(new MonitoredServiceActor.GetRecords(), 1L, TimeUnit.SECONDS)

        sender.send(records)
    }

    /**
     *
     * The ResetAlarm message that takes an argument is specific to the Mission Control Actor. These messages
     *   should only come from the Service Manager on behalf of external OSGi services/calls.
     *
     * The actor will then turn around and send a ResetAlarm message specific to the Monitored Service Actor. If no
     *   explicit canonicalMonitorName is defined, all monitors will receive the message. They may choose, however, to disregard the
     *   message.
     *
     * @param message
     */
    void onMessage(ResetAlarm message) {

        if (message.canonicalMonitorName) {

            log.info("Got a clear alarm request for ${message.canonicalMonitorName}...")

            def key = monitors.keySet().find { it.canonicalMonitorName == message.canonicalMonitorName }
            monitors.get(key) << new MonitoredServiceActor.ResetAlarm()
        } else {

            log.info("Got a clear alarm request for all monitors...")

            monitors.values().each { it << new MonitoredServiceActor.ResetAlarm() }
        }
    }

    /**
     *
     * BroadcastPollResponse messages come from the MonitoredServiceActor(s). They indicate a request to have their responses
     *   sent to all poll response handlers. Poll response handlers may choose, however, to disregard the message.
     *
     * @param message
     */
    void onMessage(MonitoredServiceActor.BroadcastPollResponse message) {

        log.info("Received BroadcastPollResponse ${message}. Relaying message to ${pollResponseHandlers.size()} poll response handlers")

        pollResponseHandlers.values().each { it << message }
    }

    /**
     *
     * BroadcastAlarm messages come from the MonitoredServiceActor(s). This message occurs when a canonicalMonitorName has reached an
     *   alarmed state based on poll results. The threshold is defined per canonicalMonitorName.
     *
     * The Mission Control actor will send each, entire record set to all notification agents. Notification agents may
     *   choose, however, to disregard the message. Mission Control will also send persistence requests to all persistence
     *   services if the canonicalMonitorName is configured to immediately persist.
     *
     * @param message
     */
    void onMessage(MonitoredServiceActor.BroadcastAlarm message) {

        log.info("Received BroadcastAlarm ${message}. Relaying message to ${notificationAgents.size()} notification agents")
        notificationAgents.values().each { it << message }

        if (message.persistImmediately) {

            log.info("BroadcastAlarm indicates immediate persistence. Sending persistence request to ${recordPersistenceServices.size()} persistence services")
            recordPersistenceServices.values().each { it << new RecordPersistenceServiceActor.PersistRecord(recordHolder: message.recordHolder) }
        }
    }

    void instantiateMonitoredServiceActor(MonitoredServiceWrapper wrapper) {

        if (recordPersistenceServices.isEmpty()) {

            log.info("No record persistence services to poll for data, starting a clean actor...")

            MonitoredServiceActor actor

            // INSTANTIATE A NEW ACTOR WITH AN EMPTY RECORD HOLDER
            actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this, recordHolder: RecordHolder.CREATE_NEW(wrapper))
            actor.start()

            monitors.put(wrapper, actor)

        } else {

            /**
             * Sort the persistence services by ranking and get the first (highest order ranking). Use the obtained
             *   key to get a handle on the actor instance and send a message requesting the Record data. The message
             *   transmission is non-blocking. Its response will invoke the closure, providing the record holder from
             *   the persistence service, and start the actor with history.
             */
            def persistenceWrapper = recordPersistenceServices.keySet().sort { it.definition.ranking() }.first()

            log.info("Polling ${persistenceWrapper.service.class} for records...")

            recordPersistenceServices.get(persistenceWrapper).sendAndContinue(new RecordPersistenceServiceActor.GetRecord(canonicalMonitorName: wrapper.canonicalMonitorName), { Optional<RecordHolder> recordHolder ->

                log.info("Received record ${recordHolder} from persistence service")

                MonitoredServiceActor actor

                if (recordHolder.present) {

                    log.info("Record holder is present, setting record holder on actor, starting actor...")
                    actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this, recordHolder: recordHolder.get())
                } else {

                    log.info("Record holder is absent, starting a clean actor...")
                    actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this, recordHolder: RecordHolder.CREATE_NEW(wrapper))
                }

                actor.start()
                monitors.put(wrapper, actor)
            })
        }
    }

    void onMessage(NotificationAgentActor.GetStatistics message) {

        log.info("Got a record request for ${message.canonicalAgentName}")

        NotificationAgentWrapper key = notificationAgents.keySet().find { it.agent.class.canonicalName == message.canonicalAgentName }
        Statistics records = notificationAgents.get(key).sendAndWait(new NotificationAgentActor.GetStatistics(), 1L, TimeUnit.SECONDS)

        sender.send(records)
    }

    void onMessage(PollResponseHandlerActor.GetStatistics message) {

        log.info("Got a record request for ${message.canonicalResponseHandler}")

        PollResponseWrapper key = pollResponseHandlers.keySet().find { it.handler.class.canonicalName == message.canonicalResponseHandler }
        Statistics records = pollResponseHandlers.get(key).sendAndWait(new PollResponseHandlerActor.GetStatistics(), 1L, TimeUnit.SECONDS)

        sender.send(records)
    }

    void onMessage(RecordPersistenceServiceActor.GetStatistics message) {

        log.info("Got a record request for ${message.canonicalPersistenceHandler}")

        RecordPersistenceServiceWrapper key = recordPersistenceServices.keySet().find { it.service.class.canonicalName == message.canonicalPersistenceHandler }
        Statistics records = recordPersistenceServices.get(key).sendAndWait(new RecordPersistenceServiceActor.GetStatistics(), 1L, TimeUnit.SECONDS)

        sender.send(records)
    }

}
