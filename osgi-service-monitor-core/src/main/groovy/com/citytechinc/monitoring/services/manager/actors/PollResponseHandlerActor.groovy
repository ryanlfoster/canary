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

    /**
     *
     * Performs filtering of DetailedPollResponse messages. If the wrapper definition
     *   allows the message to pass, we will call the handler. This call is blocking.
     *
     *   If the wrapper definition does not allow the message to pass, the message is dropped.
     *
     * @param message
     */
    void onMessage(DetailedPollResponse message) {

        switch (wrapper.definition.strategy()) {

            case SubscriptionStrategy.opt_into:

                if (wrapper.definition.specifics().collect { it.name }.contains(message.monitoredServiceClassname))
                    wrapper.handler.handleResponse(message)

                break

            case SubscriptionStrategy.opt_out_of:

                if (!wrapper.definition.specifics().collect { it.name }.contains(message.monitoredServiceClassname))
                    wrapper.handler.handleResponse(message)

                break

            case SubscriptionStrategy.all:
            default:

                wrapper.handler.handleResponse(message)
        }
    }
}
