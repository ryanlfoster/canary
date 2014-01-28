package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.notification.SubscriptionStrategy
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.manager.actors.monitor.MonitoredServiceActor
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

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

    void onMessage(MonitoredServiceActor.BroadcastPollResponse message) {

        if (((wrapper.definition.strategy() == SubscriptionStrategy.opt_into) && (wrapper.definition.specifics().collect { it.name }.contains(message.canonicalMonitorName)))
            || ((wrapper.definition.strategy() == SubscriptionStrategy.opt_out_of) && (!wrapper.definition.specifics().collect { it.name }.contains(message.canonicalMonitorName)))
            || (wrapper.definition.strategy() == SubscriptionStrategy.all)) {

            try {
                wrapper.handler.handleResponse(message.canonicalMonitorName, message.pollResponse)
            } catch (Exception e) {
                log.error("An exception occurred calling the poll response handler: ${wrapper.handler.class.canonicalName}", e)
            }
        }
    }
}
