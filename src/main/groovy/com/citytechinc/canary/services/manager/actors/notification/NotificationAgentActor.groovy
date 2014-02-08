package com.citytechinc.canary.services.manager.actors.notification

import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.monitor.RecordHolder
import com.citytechinc.canary.api.notification.SubscriptionStrategy
import com.citytechinc.canary.services.manager.actors.MissionControlActor
import com.citytechinc.canary.services.manager.actors.Statistics
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

    static String jobprefix = 'canaryna-'

    // MESSAGES
    static class FlushQueue { }

    NotificationAgentWrapper wrapper
    Scheduler scheduler
    Statistics statistics = new Statistics()

    void afterStop() {

        scheduler.removeJob(jobprefix + wrapper.agent.class.name)
    }

    Map<String, RecordHolder> queuedMessages = [:]

    void onMessage(MissionControlActor.GetStatistics message) {
        sender.send(statistics.clone())
    }

    void onMessage(RecordHolder message) {

        ++statistics.deliveredMessages

        if (((wrapper.definition.strategy() == SubscriptionStrategy.OPT_INTO) && (wrapper.definition.specifics().contains(message.canonicalMonitorName)))
                || ((wrapper.definition.strategy() == SubscriptionStrategy.OPT_OUT_OF) && (!wrapper.definition.specifics().contains(message.canonicalMonitorName)))
                || (wrapper.definition.strategy() == SubscriptionStrategy.ALL)) {

            handleMessage(message)
        }
    }

    void onMessage(FlushQueue message) {

        log.debug("Flushing queue of size ${queuedMessages.size()}")

        Stopwatch stopwatch = Stopwatch.createStarted()

        try {

            wrapper.agent.notify(queuedMessages.values() as List<RecordHolder>)
            ++statistics.processedMessages

        } catch (Exception e) {

            log.error("An exception occurred while flushing the message queue, calling the notification agent", e)
            ++statistics.messageExceptions
        }

        queuedMessages.clear()

        statistics.addAndCalculateAverageProcessTime(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS))
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

            try {

                wrapper.agent.notify([message])
                ++statistics.processedMessages

            } catch (Exception e) {

                log.error("An exception occurred calling the notification agent", e)
                ++statistics.messageExceptions
            }

            statistics.addAndCalculateAverageProcessTime(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS))
        }
    }

}
