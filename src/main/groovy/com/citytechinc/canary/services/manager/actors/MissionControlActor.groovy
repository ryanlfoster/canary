package com.citytechinc.canary.services.manager.actors

import com.citytechinc.canary.api.monitor.DetailedPollResponse
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.notification.AlarmNotification
import com.citytechinc.canary.api.notification.AlarmResetNotification
import com.citytechinc.canary.api.notification.NotificationAgent
import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.persistence.RecordPersistenceService
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.canary.api.responsehandler.PollResponseHandler
import com.citytechinc.canary.api.responsehandler.PollResponseHandlerWrapper
import com.citytechinc.canary.api.monitor.RecordHolder
import com.citytechinc.canary.services.manager.actors.monitor.MonitoredServiceActor
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

    final static String SCHEDULER_KEY = 'canarymisc-'

    // MESSAGES

    static class InstantiateMonitors { }
    static class RequestAllMonitorsPoll { }
    static class RequestAllMonitorsPersist { }
    static class RequestAllMonitorsResetIfAlarmed { }
    static class RequestMonitorResetIfAlarmed {

        String identifier
    }

    static class ServiceLifecycleEvent {

        def service
        Boolean isRegistration
    }

    static class GetRecords {

        String identifier
    }

    static class GetStatistics {

        public enum Type {

            NOTIFICATION_AGENT, POLL_RESPONSE_HANDLER, RECORD_PERSISTENCE_SERVICE
        }

        Type type
        String identifier
    }

    Scheduler scheduler
    Long instantiateActorMessageTimeout

    Map<MonitoredServiceWrapper, MonitoredServiceActor> monitors = [:]
    Map<NotificationAgentWrapper, NotificationAgentActor> notificationAgents = [:]
    Map<PollResponseHandlerWrapper, PollResponseHandlerActor> pollResponseHandlers = [:]
    Map<RecordPersistenceServiceWrapper, RecordPersistenceServiceActor> recordPersistenceServices = [:]

    Boolean initialInstantiationOfActorsHasOccurred = false

    void afterStart() {

        log.debug("Adding scheduled job defined under the key: ${SCHEDULER_KEY}")

        final Date now = new Date()
        scheduler.fireJobAt(SCHEDULER_KEY, {

            this << new InstantiateMonitors()

        }, [:], new Date(now.time + TimeUnit.MILLISECONDS.convert(instantiateActorMessageTimeout, TimeUnit.SECONDS)))
    }

    void onMessage(GetStatistics message) {

        def actor

        if (message.type == GetStatistics.Type.NOTIFICATION_AGENT && notificationAgents.keySet().find { it.identifier == message.identifier}) {

            actor = notificationAgents.get(notificationAgents.keySet().find { it.identifier == message.identifier })

        } else if (message.type == GetStatistics.Type.POLL_RESPONSE_HANDLER && pollResponseHandlers.keySet().find { it.identifier == message.identifier}) {

            actor = pollResponseHandlers.get(pollResponseHandlers.keySet().find { it.identifier == message.identifier })

        } else if (message.type == GetStatistics.Type.RECORD_PERSISTENCE_SERVICE && recordPersistenceServices.keySet().find { it.identifier == message.identifier}) {

            actor = recordPersistenceServices.get(recordPersistenceServices.keySet().find { it.identifier == message.identifier })
        }

        Optional<Statistics> statistics
        statistics = actor ? Optional.of(actor.sendAndWait(message)) : Optional.absent()

        sender.send(statistics)
    }

    void onMessage(GetRecords message) {

        def actor

        if (monitors.keySet().find { it.identifier == message.identifier }) {

            actor = monitors.get(monitors.keySet().find { it.identifier == message.identifier })
        }

        Optional<RecordHolder> records
        records = actor ? Optional.of(actor.sendAndWait(new MonitoredServiceActor.GetRecord())) : Optional.absent()

        sender.send(records)
    }

    void onMessage(InstantiateMonitors message) {

        log.debug("Starting ${monitors.size()} monitors")

        initialInstantiationOfActorsHasOccurred = true

        monitors.keySet().each { MonitoredServiceWrapper wrapper ->

            instantiateMonitoredServiceActor(wrapper)
        }
    }

    void onMessage(ServiceLifecycleEvent message) {

        if (message.service instanceof MonitoredService) {

            MonitoredServiceWrapper wrapper = new MonitoredServiceWrapper(message.service)

            if (message.isRegistration && !monitors.containsKey(wrapper)) {

                if (initialInstantiationOfActorsHasOccurred) {
                    instantiateMonitoredServiceActor(wrapper)
                } else {
                    monitors.put(wrapper, null)
                }

            } else if (!message.isRegistration && monitors.containsKey(wrapper)) {

                log.debug("Termination actor for monitored service agent ${wrapper.identifier}")
                monitors.remove(wrapper)?.terminate()
            }

        } else if (message.service instanceof NotificationAgent) {

            NotificationAgentWrapper wrapper = new NotificationAgentWrapper(message.service)

            if (message.isRegistration && !notificationAgents.containsKey(wrapper)) {

                log.debug("Starting actor for notification agent ${wrapper.identifier}")
                NotificationAgentActor actor = new NotificationAgentActor(wrapper: wrapper, scheduler: scheduler)
                actor.start()

                notificationAgents.put(wrapper, actor)

            } else if (!message.isRegistration && notificationAgents.containsKey(wrapper)) {

                log.debug("Termination actor for notificationa agent ${wrapper.identifier}")
                notificationAgents.remove(wrapper)?.terminate()
            }

        } else if (message.service instanceof RecordPersistenceService) {

            RecordPersistenceServiceWrapper wrapper = new RecordPersistenceServiceWrapper(message.service)

            if (message.isRegistration && !recordPersistenceServices.containsKey(wrapper)) {

                if (wrapper.providesReadOperations || wrapper.providesWriteOperations) {

                    log.debug("Starting actor for persistence service ${wrapper.identifier}")
                    RecordPersistenceServiceActor actor = new RecordPersistenceServiceActor(wrapper: wrapper)
                    actor.start()

                    recordPersistenceServices.put(wrapper, actor)
                } else {

                    log.debug("Persistence Service ${wrapper.identifier} does not support read or write operations, thus we are dropping from registration")
                }

            } else if (!message.isRegistration && recordPersistenceServices.containsKey(wrapper)) {

                log.debug("Terminating actor for persistence service ${wrapper.identifier}")
                recordPersistenceServices.remove(wrapper)?.terminate()
            }

        } else if (message.service instanceof PollResponseHandler) {

            PollResponseHandlerWrapper wrapper = new PollResponseHandlerWrapper(message.service)

            if (message.isRegistration && !pollResponseHandlers.containsKey(wrapper)) {

                log.debug("Starting actor for poll response handler ${wrapper.identifier}")
                PollResponseHandlerActor actor = new PollResponseHandlerActor(wrapper: wrapper)
                actor.start()

                pollResponseHandlers.put(wrapper, actor)

            } else if (!message.isRegistration && pollResponseHandlers.containsKey(wrapper)) {

                log.debug("Terminating actor for poll response handler ${wrapper.identifier}")
                pollResponseHandlers.remove(wrapper)?.terminate()
            }
        }
    }

    void onMessage(RequestAllMonitorsPersist message) {

        monitors.values().each { MonitoredServiceActor actor ->

            actor.sendAndContinue(new MonitoredServiceActor.GetRecord(), { RecordHolder recordHolder ->

                recordPersistenceServices.values().each { RecordPersistenceServiceActor persistenceActor ->

                    persistenceActor << new RecordPersistenceServiceActor.PersistRecord(recordHolder: recordHolder)
                }
            })
        }
    }

    void onMessage(RequestAllMonitorsPoll message) {

        monitors.values().each { it << new MonitoredServiceActor.Poll() }
    }

    void onMessage(RequestMonitorResetIfAlarmed message) {

        MonitoredServiceWrapper wrapper = monitors.keySet().find { it.identifier == message.identifier }

        if (wrapper) {

            monitors.get(wrapper) << new MonitoredServiceActor.ResetAlarm()
        }
    }

    void onMessage(RequestAllMonitorsResetIfAlarmed message) {

        monitors.values().each { it << new MonitoredServiceActor.ResetAlarm()}
    }

    void onMessage(AlarmNotification message) {

        notificationAgents.values().each { NotificationAgentActor actor ->

            actor << message
        }

        // IF THE MONITOR DEFINITION STATES PERSISTENCE WHEN ALARMED, SEND RECORD HOLDERS TO PERSISTENCE SERVICES
        if (monitors.keySet().find { it.identifier == message.recordHolder.monitorIdentifier }?.persistWhenAlarmed()) {

            log.debug("Service monitor ${message.recordHolder.monitorIdentifier} is configured to persist when alarmed. Sending" +
                    " persist message to ${recordPersistenceServices.size()} record persistence services")

            recordPersistenceServices.values().each { RecordPersistenceServiceActor actor ->

                actor << new RecordPersistenceServiceActor.PersistRecord(recordHolder: message.recordHolder)
            }
        }
    }

    void onMessage(AlarmResetNotification message) {

        notificationAgents.values().each {
            it << message
        }
    }

    void onMessage(PollResponseHandlerActor.PollResponseReceipt message) {

        pollResponseHandlers.values().each { PollResponseHandlerActor actor ->

            actor << message
        }
    }

    void instantiateMonitoredServiceActor(MonitoredServiceWrapper wrapper) {

        if (recordPersistenceServices.isEmpty()) {

            log.debug("No record persistence services to poll for data, starting a clean actor")

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
            RecordPersistenceServiceWrapper persistenceWrapper = recordPersistenceServices.keySet().findAll { it.providesReadOperations }.sort { it.ranking }.first()
            RecordPersistenceServiceActor persistenceActor = recordPersistenceServices.get(persistenceWrapper)

            log.debug("Polling ${persistenceWrapper.identifier} for records")

            persistenceActor.sendAndContinue(new RecordPersistenceServiceActor.GetPersistedRecord(identifier: wrapper.identifier), { Optional<List<DetailedPollResponse>> pollResponses ->

                MonitoredServiceActor actor

                if (pollResponses.present) {

                    log.debug("Records present for service ${wrapper.identifier}, using records to start actor")
                    actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this, recordHolder: RecordHolder.CREATE_FROM_RECORDS(wrapper, pollResponses.get()))
                } else {

                    log.debug("Records absent for service ${wrapper.identifier}, starting a clean actor")
                    actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this, recordHolder: RecordHolder.CREATE_NEW(wrapper))
                }

                actor.start()
                monitors.put(wrapper, actor)
            })
        }
    }
}