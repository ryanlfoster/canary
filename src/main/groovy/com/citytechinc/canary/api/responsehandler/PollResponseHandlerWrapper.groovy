package com.citytechinc.canary.api.responsehandler

import com.citytechinc.canary.api.notification.SubscriptionStrategy
import groovy.transform.EqualsAndHashCode

/**
 *
 * @author Josh Durbin
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@EqualsAndHashCode
public class PollResponseHandlerWrapper {

    @Delegate
    final PollResponseHandler handler

    final String identifier
    final SubscriptionStrategy strategy
    final List<String> specifics

    public PollResponseHandlerWrapper(PollResponseHandler handler) {

        this.handler = handler

        def definition = handler.getClass().getAnnotation(PollResponseHandlerDefinition)

        identifier = handler.class.canonicalName
        strategy = definition.strategy()
        specifics = definition.specifics()
    }
}
