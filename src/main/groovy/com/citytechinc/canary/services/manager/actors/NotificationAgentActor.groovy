package com.citytechinc.canary.services.manager.actors

import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.monitor.RecordHolder
import com.google.common.base.Stopwatch
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
final class NotificationAgentActor extends DynamicDispatchActor {

    static String jobprefix = 'scheduled-notification-agent-flush-'

    // MESSAGES
    static class FlushQueue { }

    NotificationAgentWrapper wrapper
    Scheduler scheduler
    MissionControlActor missionControl

    void afterStop() {

        scheduler.removeJob(jobprefix + wrapper.agent.class.name)
    }

    Map<String, RecordHolder> queuedMessages = [:]

    void onMessage(RecordHolder message) {

        if (((wrapper.definition.strategy() == SubscriptionStrategy.opt_into) && (wrapper.definition.specifics().contains(message.canonicalMonitorName)))
                || ((wrapper.definition.strategy() == SubscriptionStrategy.opt_out_of) && (!wrapper.definition.specifics().contains(message.canonicalMonitorName)))
                || (wrapper.definition.strategy() == SubscriptionStrategy.all)) {

            handleMessage(message)
        }
    }

    void onMessage(FlushQueue message) {

        log.debug("Flushing queue of size ${queuedMessages.size()}")

        Stopwatch stopwatch = Stopwatch.createStarted()
        Boolean exceptionOccurred = false

        try {
            wrapper.agent.notify(queuedMessages.values() as List<RecordHolder>)
        } catch (Exception e) {
            exceptionOccurred = true
            log.error("An exception occurred while flushing the message queue, calling the notification agent", e)
        }

        queuedMessages.clear()

        missionControl << new MissionControlActor.InternalProcessAccounting(
                recordType: MissionControlActor.RecordType.NOTIFICATION_AGENT,
                processTime: stopwatch.stop().elapsed(TimeUnit.MILLISECONDS),
                identifier: wrapper.agent.class.canonicalName,
                exceptionOccurred: exceptionOccurred)
    }

    private handleMessage(RecordHolder message) {

        if (wrapper.aggregationWindowInMilliseconds > 0) {

            if (queuedMessages.isEmpty()) {

                Date now = new Date()
                scheduler.fireJobAt(jobprefix + wrapper.agent.class.name, {

                    this << new FlushQueue()

                }, [:], new Date(now.time + wrapper.aggregationWindowInMilliseconds))
            }

            log.debug("Adding message to queue with size of ${queuedMessages.size()}")
            queuedMessages.put(message.canonicalMonitorName, message)

        } else {

            log.debug("Aggregation undefined, sending message without delay...")

            Stopwatch stopwatch = Stopwatch.createStarted()
            Boolean exceptionOccurred = false

            try {
                wrapper.agent.notify([message])
            } catch (Exception e) {
                exceptionOccurred = true
                log.error("An exception occurred calling the notification agent", e)
            }

            missionControl << new MissionControlActor.InternalProcessAccounting(
                    recordType: MissionControlActor.RecordType.NOTIFICATION_AGENT,
                    processTime: stopwatch.stop().elapsed(TimeUnit.MILLISECONDS),
                    identifier: wrapper.agent.class.canonicalName,
                    exceptionOccurred: exceptionOccurred)
        }
    }

}
