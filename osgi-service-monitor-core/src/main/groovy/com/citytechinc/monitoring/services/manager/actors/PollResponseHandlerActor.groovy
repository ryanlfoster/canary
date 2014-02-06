package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.notification.SubscriptionStrategy
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.manager.actors.monitor.MonitoredServiceActor
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

    PollResponseWrapper wrapper
    MissionControlActor missionControl

    void onMessage(MissionControlActor.PollResponseReceipt message) {

        if (((wrapper.definition.strategy() == SubscriptionStrategy.opt_into) && (wrapper.definition.specifics().contains(message.identifier)))
            || ((wrapper.definition.strategy() == SubscriptionStrategy.opt_out_of) && (!wrapper.definition.specifics().contains(message.identifier)))
            || (wrapper.definition.strategy() == SubscriptionStrategy.all)) {

            Stopwatch stopwatch = Stopwatch.createStarted()
            Boolean exceptionOccurred = false

            try {
                wrapper.handler.handleResponse(message.identifier, message.detailedPollResponse)
            } catch (Exception e) {
                exceptionOccurred = true
                log.error("An exception occurred calling the poll response handler: ${wrapper.handler.class.canonicalName}", e)
            }

            missionControl << new MissionControlActor.InternalProcessAccounting(
                    recordType: MissionControlActor.RecordType.POLL_RESPONSE_HANDLER,
                    processTime: stopwatch.stop().elapsed(TimeUnit.MILLISECONDS),
                    identifier: wrapper.handler.class.canonicalName,
                    exceptionOccurred: exceptionOccurred)
        }
    }
}
