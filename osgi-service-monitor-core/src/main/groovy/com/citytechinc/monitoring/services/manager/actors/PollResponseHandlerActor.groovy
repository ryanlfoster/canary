package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.notification.SubscriptionStrategy
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import groovyx.gpars.actor.DynamicDispatchActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
final class PollResponseHandlerActor extends DynamicDispatchActor {

    PollResponseWrapper wrapper

    void onMessage(MonitoredServiceActor.BroadcastPollResponse message) {

        if (((wrapper.definition.strategy() == SubscriptionStrategy.opt_into) && (wrapper.definition.specifics().collect { it.name }.contains(message.canonicalMonitorName)))
            || ((wrapper.definition.strategy() == SubscriptionStrategy.opt_out_of) && (!wrapper.definition.specifics().collect { it.name }.contains(message.canonicalMonitorName)))
            || (wrapper.definition.strategy() == SubscriptionStrategy.all)) {

            wrapper.handler.handleResponse(message.canonicalMonitorName, message.pollResponse)
        }
    }
}
