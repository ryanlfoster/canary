package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.notification.SubscriptionStrategy
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.manager.actors.monitor.MonitoredServiceActor
import com.citytechinc.monitoring.services.manager.actors.monitor.Statistics
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

    // MESSAGES
    static class GetStatistics { String canonicalResponseHandler }

    PollResponseWrapper wrapper
    Statistics statistics = new Statistics()

    void onMessage(GetStatistics message) {

        log.info("Received statistics request for ${wrapper.class.canonicalName}")

        sender.send(statistics.clone())
    }

    void onMessage(MonitoredServiceActor.BroadcastPollResponse message) {

        if (((wrapper.definition.strategy() == SubscriptionStrategy.opt_into) && (wrapper.definition.specifics().collect { it.name }.contains(message.canonicalMonitorName)))
            || ((wrapper.definition.strategy() == SubscriptionStrategy.opt_out_of) && (!wrapper.definition.specifics().collect { it.name }.contains(message.canonicalMonitorName)))
            || (wrapper.definition.strategy() == SubscriptionStrategy.all)) {

            Stopwatch stopwatch = Stopwatch.createStarted()

            try {
                ++statistics.numberOfProcessedMessages
                wrapper.handler.handleResponse(message.canonicalMonitorName, message.pollResponse)
            } catch (Exception e) {
                ++statistics.numberOfMessageExceptions
                log.error("An exception occurred calling the poll response handler: ${wrapper.handler.class.canonicalName}", e)
            }

            stopwatch.stop()
            statistics.addProcessTime(stopwatch.elapsed(TimeUnit.MILLISECONDS))
        }
    }
}
