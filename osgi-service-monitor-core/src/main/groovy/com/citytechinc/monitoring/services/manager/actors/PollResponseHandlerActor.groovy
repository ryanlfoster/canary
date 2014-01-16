package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.notification.SubscriptionStrategy
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
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

    void onMessage(DetailedPollResponse message) {

        switch (wrapper.definition.subscriptionStrategy()) {

            case SubscriptionStrategy.opt_into:

                if (wrapper.definition.subscriptionStrategySpecifics().collect { it.name }.contains(message.monitoredServiceClassname))
                    wrapper.handler.handleResponse(message)

                break

            case SubscriptionStrategy.opt_out_of:

                if (!wrapper.definition.subscriptionStrategySpecifics().collect { it.name }.contains(message.monitoredServiceClassname))
                    wrapper.handler.handleResponse(message)

                break

            case SubscriptionStrategy.all:
            default:

                wrapper.handler.handleResponse(message)
        }
    }
}
