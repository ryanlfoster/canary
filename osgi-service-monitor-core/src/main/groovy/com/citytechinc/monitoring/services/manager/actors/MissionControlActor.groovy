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
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import com.citytechinc.monitoring.services.manager.actors.monitor.MonitoredServiceActor
import com.google.common.base.Optional
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor
import org.apache.sling.commons.scheduler.Scheduler

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Slf4j
final class MissionControlActor extends DynamicDispatchActor {

    public enum RecordType {

        MONITOR, NOTIFICATION_AGENT, POLL_RESPONSE_HANDLER, RECORD_PERSISTENCE_SERVICE
    }

    // MESSAGES
    static class ResetAlarm { String identifier }
    static class InstantiateMonitors { }

    static class RequestAllMonitorsPoll { }
    static class RequestAllMonitorsPersist { }

    static class PollResponseReceipt {

        DetailedPollResponse detailedPollResponse
        String identifier
    }

    static class ServiceLifecycleEvent {

        def service
        Boolean isRegistration
    }

    static class InternalProcessAccounting {

        RecordType recordType
        Long processTime
        String identifier
        Boolean exceptionOccurred
    }

    static class GetRecords {

        String identifier
    }

    static class GetStatistics {

        RecordType recordType
        String identifier
    }

    Scheduler scheduler

    Map<MonitoredServiceWrapper, MonitoredServiceActor> monitors = [:]
    Map<NotificationAgentWrapper, NotificationAgentActor> notificationAgents = [:]
    Map<PollResponseWrapper, PollResponseHandlerActor> pollResponseHandlers = [:]
    Map<RecordPersistenceServiceWrapper, RecordPersistenceServiceActor> recordPersistenceServices = [:]

    Map<String, RecordHolder> monitorRecords = [:]
    Map<String, Statistics> notificationAgentStatistics = [:]
    Map<String, Statistics> pollResponseHandlerStatistics = [:]
    Map<String, Statistics> recordPersistenceServiceStatistics = [:]

    Boolean hasPassedMonitorActorInstantiationTimeout = false

    void onMessage(GetStatistics message) {

        Optional<Statistics> retrievedStatistics = Optional.absent()

        if (message.recordType == RecordType.NOTIFICATION_AGENT && notificationAgentStatistics.containsKey(message.identifier)) {

            retrievedStatistics = Optional.of(notificationAgentStatistics.get(message.identifier))
        } else if (message.recordType == RecordType.POLL_RESPONSE_HANDLER && pollResponseHandlerStatistics.containsKey(message.identifier)) {

            retrievedStatistics = Optional.of(pollResponseHandlerStatistics.get(message.identifier))
        } else if (message.recordType == RecordType.RECORD_PERSISTENCE_SERVICE && recordPersistenceServiceStatistics.containsKey(message.identifier)) {

            retrievedStatistics = Optional.of(recordPersistenceServiceStatistics.get(message.identifier))
        }

        sender.send(retrievedStatistics)
    }

    void onMessage(GetRecords message) {

        Optional<RecordHolder> retrievedRecords = Optional.absent()

        if (monitorRecords.containsKey(message.identifier)) {
            retrievedRecords = Optional.of(monitorRecords.get(message.identifier))
        }

        sender.send(retrievedRecords)
    }

    void onMessage(InternalProcessAccounting message) {

        Statistics statistics = null

        if (message.recordType == RecordType.NOTIFICATION_AGENT) {
            statistics = notificationAgentStatistics.get(message.identifier)
        } else if (message.recordType == RecordType.POLL_RESPONSE_HANDLER) {
            statistics = pollResponseHandlerStatistics.get(message.identifier)
        } else if (message.recordType == RecordType.RECORD_PERSISTENCE_SERVICE) {
            statistics = recordPersistenceServiceStatistics.get(message.identifier)
        }

        if (statistics) {

            ++statistics.processedMessages

            if (message.exceptionOccurred) {
                ++statistics.messageExceptions
            }

            statistics.addAndCalculateAverageProcessTime(message.processTime)
        }
    }

    void onMessage(InstantiateMonitors message) {

        log.debug("Starting ${monitors.size()} monitors...")

        hasPassedMonitorActorInstantiationTimeout = true

        monitors.keySet().each { MonitoredServiceWrapper wrapper ->

            instantiateMonitoredServiceActor(wrapper)
        }
    }

    void onMessage(ServiceLifecycleEvent message) {

        if (message.service instanceof MonitoredService) {

            MonitoredServiceWrapper wrapper = new MonitoredServiceWrapper(message.service)

            if (message.isRegistration && !monitors.containsKey(wrapper)) {

                if (hasPassedMonitorActorInstantiationTimeout) {
                    instantiateMonitoredServiceActor(wrapper)
                } else {
                    monitors.put(wrapper, null)
                }

            } else if (!message.isRegistration && monitors.containsKey(wrapper)) {

                monitors.remove(wrapper)?.terminate()
            }

        } else if (message.service instanceof NotificationAgent) {

            NotificationAgentWrapper wrapper = new NotificationAgentWrapper(message.service)

            if (message.isRegistration && !notificationAgents.containsKey(wrapper)) {

                NotificationAgentActor actor = new NotificationAgentActor(wrapper: wrapper, scheduler: scheduler, missionControl: this)
                actor.start()

                notificationAgents.put(wrapper, actor)
                notificationAgentStatistics.put(wrapper.agent.class.canonicalName, new Statistics())

            } else if (!message.isRegistration && notificationAgents.containsKey(wrapper)) {

                notificationAgents.remove(wrapper)?.terminate()
                notificationAgentStatistics.remove(wrapper.agent.class.canonicalName)
            }

        } else if (message.service instanceof RecordPersistenceService) {

            RecordPersistenceServiceWrapper wrapper = new RecordPersistenceServiceWrapper(message.service)

            if (message.isRegistration && !recordPersistenceServices.containsKey(wrapper)) {

                RecordPersistenceServiceActor actor = new RecordPersistenceServiceActor(wrapper: wrapper, missionControl: this)
                actor.start()

                recordPersistenceServices.put(wrapper, actor)
                recordPersistenceServiceStatistics.put(wrapper.service.class.canonicalName, new Statistics())

            } else if (!message.isRegistration && recordPersistenceServices.containsKey(wrapper)) {

                recordPersistenceServices.remove(wrapper)?.terminate()
                recordPersistenceServiceStatistics.remove(wrapper.service.class.canonicalName)
            }

        } else if (message.service instanceof PollResponseHandler) {

            PollResponseWrapper wrapper = new PollResponseWrapper(message.service)

            if (message.isRegistration && !pollResponseHandlers.containsKey(wrapper)) {

                PollResponseHandlerActor actor = new PollResponseHandlerActor(wrapper: wrapper, missionControl: this)
                actor.start()

                pollResponseHandlers.put(wrapper, actor)
                pollResponseHandlerStatistics.put(wrapper.handler.class.canonicalName, new Statistics())

            } else if (!message.isRegistration && pollResponseHandlers.containsKey(wrapper)) {

                pollResponseHandlers.remove(wrapper)?.terminate()
                pollResponseHandlerStatistics.remove(wrapper.handler.class.canonicalName)
            }
        }
    }

    void onMessage(RequestAllMonitorsPersist message) {

        monitorRecords.values().each { RecordHolder recordHolder ->

            recordPersistenceServices.values().each { RecordPersistenceServiceActor actor ->

                actor << new RecordPersistenceServiceActor.PersistRecord(recordHolder: recordHolder)
            }
        }
    }

    void onMessage(RequestAllMonitorsPoll message) {

        monitors.values().each { it << new MonitoredServiceActor.Poll() }
    }

    void onMessage(ResetAlarm message) {

        if (message.identifier) {

            log.info("Resetting alarm for monitor ${message.identifier}")
            monitorRecords.get(message.identifier).resetAlarm()

        } else {

            log.trace("Received a global alarm reset, issuing reset to all monitor records")

            monitorRecords.values().each { RecordHolder recordHolder ->

                if (recordHolder.isAlarmed()) {

                    log.info("Resetting alarm for monitor ${recordHolder.canonicalMonitorName}")
                    recordHolder.resetAlarm()
                }
            }
        }
    }

    void onMessage(PollResponseReceipt message) {

        // ADD POLL RESPONSE
        def recordHolder = monitorRecords.get(message.identifier)
        recordHolder.addRecord(message.detailedPollResponse)

        // PASS RESPONSE ON TO HANDLERS
        pollResponseHandlers.keySet().each { PollResponseWrapper pollResponseWrapper ->

            def actor = pollResponseHandlers.get(pollResponseWrapper)

            ++pollResponseHandlerStatistics.get(pollResponseWrapper.handler.class.canonicalName).deliveredMessages
            actor << message
        }

        if (recordHolder.isAlarmed()) {

            // IF THE MONITOR IS ALARMED, SEND THE RECORD HOLDER TO NOTIFICATION AGENTS
            notificationAgents.keySet().each { NotificationAgentWrapper notificationAgentWrapper ->

                def actor = notificationAgents.get(notificationAgentWrapper)

                ++notificationAgentStatistics.get(notificationAgentWrapper.agent.class.canonicalName).deliveredMessages
                actor << recordHolder.clone()
            }

            // IF THE MONITOR DEFINITION STATES PERSISTENCE WHEN ALARMED, SEND RECORD HOLDERS TO PERSISTENCE SERVICES
            if (monitors.keySet().find { it.canonicalMonitorName == recordHolder.canonicalMonitorName }?.definition?.persistWhenAlarmed()) {

                recordPersistenceServices.values().each { RecordPersistenceServiceActor actor ->

                    actor << new RecordPersistenceServiceActor.PersistRecord(recordHolder: recordHolder)
                }
            }
        }
    }

    void instantiateMonitoredServiceActor(MonitoredServiceWrapper wrapper) {

        if (recordPersistenceServices.isEmpty()) {

            monitorRecords.put(wrapper.canonicalMonitorName, RecordHolder.CREATE_NEW(wrapper))

            log.debug("No record persistence services to poll for data, starting a clean actor...")

            MonitoredServiceActor actor

            // INSTANTIATE A NEW ACTOR WITH AN EMPTY RECORD HOLDER
            actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this)
            actor.start()

            monitors.put(wrapper, actor)
            //monitorRecords.put(wrapper.canonicalMonitorName, RecordHolder.CREATE_NEW(wrapper))

        } else {

            /**
             * Sort the persistence services by ranking and get the first (highest order ranking). Use the obtained
             *   key to get a handle on the actor instance and send a message requesting the Record data. The message
             *   transmission is non-blocking. Its response will invoke the closure, providing the record holder from
             *   the persistence service, and start the actor with history.
             */
            def persistenceWrapper = recordPersistenceServices.keySet().sort { it.definition.ranking() }.first()

            log.debug("Polling ${persistenceWrapper.service.class} for records...")

            recordPersistenceServices.get(persistenceWrapper).sendAndContinue(new RecordPersistenceServiceActor.GetRecord(canonicalMonitorName: wrapper.canonicalMonitorName), { Optional<RecordHolder> recordHolder ->

                monitorRecords.put(wrapper.canonicalMonitorName, RecordHolder.CREATE_NEW(wrapper))

                log.debug("Received record ${recordHolder} from persistence service")

                MonitoredServiceActor actor

                if (recordHolder.present) {

                    actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this)
                } else {

                    actor = new MonitoredServiceActor(scheduler: scheduler, wrapper: wrapper, missionControl: this)
                }

                actor.start()
                monitors.put(wrapper, actor)
                //monitorRecords.put(wrapper.canonicalMonitorName, RecordHolder.CREATE_NEW(wrapper))
            })
        }
    }
}