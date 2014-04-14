package com.citytechinc.canary.services.manager.actors

import com.citytechinc.canary.api.monitor.PollResult
import com.citytechinc.canary.api.notification.SubscriptionStrategy
import com.citytechinc.canary.api.responsehandler.PollResponseHandlerWrapper
import com.google.common.base.Stopwatch
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
final class PollResponseHandlerActor extends DynamicDispatchActor {

    static class PollResponseReceipt {

        String identifier
        PollResult response
    }

    PollResponseHandlerWrapper wrapper
    Statistics statistics = new Statistics()

    void onMessage(MissionControlActor.GetStatistics message) {
        sender.send(statistics.clone())
    }

    void onMessage(PollResponseReceipt message) {

        ++statistics.deliveredMessages

        if (((wrapper.strategy == SubscriptionStrategy.OPT_INTO) && (wrapper.specifics.contains(message.identifier)))
            || ((wrapper.strategy == SubscriptionStrategy.OPT_OUT_OF) && (!wrapper.specifics.contains(message.identifier)))
            || (wrapper.strategy == SubscriptionStrategy.ALL)) {

            Stopwatch stopwatch = Stopwatch.createStarted()

            try {

                wrapper.handleResponse(message.identifier, message.response)
                ++statistics.processedMessages

            } catch (Exception e) {

                log.error("An exception occurred calling the poll response handler: ${wrapper.identifier} for monitored service ${message.identifier}", e)
                ++statistics.messageExceptions
            }

            Long processTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)
            log.debug("It took ${processTime} ms to calling the poll response handler: ${wrapper.identifier} for monitored service ${message.identifier}")

            statistics.addAndCalculateAverageProcessTime(processTime)
        }
    }
}
