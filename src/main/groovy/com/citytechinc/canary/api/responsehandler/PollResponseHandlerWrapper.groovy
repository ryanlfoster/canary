package com.citytechinc.canary.api.responsehandler

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

    @Delegate final PollResponseHandler handler
    final String identifier
    final PollResponseHandlerDefinition definition

    public PollResponseHandlerWrapper(PollResponseHandler handler) {
        this.handler = handler
        identifier = handler.class.canonicalName
        definition = handler.getClass().getAnnotation(PollResponseHandlerDefinition)
    }
}
