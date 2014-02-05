package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.notification.SubscriptionStrategy
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import com.citytechinc.monitoring.services.manager.actors.monitor.MonitoredServiceActor
import com.citytechinc.monitoring.services.manager.actors.monitor.Statistics
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

    static def jobprefix = 'scheduled-notification-agent-flush-'

    // MESSAGES
    static class FlushQueue { }
    static class GetStatistics { String canonicalAgentName }

    NotificationAgentWrapper wrapper
    Scheduler scheduler
    Statistics statistics = new Statistics()

    void afterStop() {

        log.debug("Removing job ${jobprefix + wrapper.agent.class.name}")
        scheduler.removeJob(jobprefix + wrapper.agent.class.name)
    }

    /**
     * Internal message queue is used for batching messages. When a message is processed and an aggregation period
     *   is defined, we place said messages in this list and start a timer. The timer is equal to the aggregation period
     *   which, when expired, instructs this actor to flush its queue (this list).
     */
    List<RecordHolder> queuedMessages = []

    void onMessage(GetStatistics message) {

        log.debug("Received statistics request for ${wrapper.class.canonicalName}")

        sender.send(statistics.clone())
    }

    /**
     *
     * Performs filtering of RecordHolder messages. If the wrapper definition
     *   allows the message to pass, we will call the handler. If the wrapper definition does
     *   not allow the message to pass, the message is dropped.
     *
     * @param message
     */
    void onMessage(MonitoredServiceActor.BroadcastAlarm message) {

        if (((wrapper.definition.strategy() == SubscriptionStrategy.opt_into) && (wrapper.definition.specifics().contains(message.recordHolder.canonicalMonitorName)))
                || ((wrapper.definition.strategy() == SubscriptionStrategy.opt_out_of) && (!wrapper.definition.specifics().contains(message.recordHolder.canonicalMonitorName)))
                || (wrapper.definition.strategy() == SubscriptionStrategy.all)) {

            handleMessage(message.recordHolder)
        }
    }

    void onMessage(FlushQueue message) {

        log.debug("Flushing queue of size ${queuedMessages.size()}")

        Stopwatch stopwatch = Stopwatch.createStarted()

        try {
            ++statistics.numberOfProcessedMessages
            wrapper.agent.notify(queuedMessages)
        } catch (Exception e) {
            ++statistics.numberOfMessageExceptions
            log.error("An exception occurred while flushing the message queue, calling the notification agent", e)
        }

        stopwatch.stop()
        statistics.addProcessTime(stopwatch.elapsed(TimeUnit.MILLISECONDS))

        queuedMessages.clear()
    }

    private handleMessage(RecordHolder message) {

        if (wrapper.aggregationWindowInMilliseconds > 0) {

            if (queuedMessages.isEmpty()) {

                final Date now = new Date()
                scheduler.fireJobAt(jobprefix + wrapper.agent.class.name, {

                    this << new FlushQueue()

                }, [:], new Date(now.time + wrapper.aggregationWindowInMilliseconds))
            }

            log.debug("Adding message to queue with size of ${queuedMessages.size()}")
            queuedMessages.add(message)

        } else {

            log.debug("Aggregation undefined, sending message without delay...")

            Stopwatch stopwatch = Stopwatch.createStarted()

            try {
                ++statistics.numberOfProcessedMessages
                wrapper.agent.notify([message])
            } catch (Exception e) {
                ++statistics.numberOfMessageExceptions
                log.error("An exception occurred calling the notification agent", e)
            }

            stopwatch.stop()
            statistics.addProcessTime(stopwatch.elapsed(TimeUnit.MILLISECONDS))
        }
    }

}
