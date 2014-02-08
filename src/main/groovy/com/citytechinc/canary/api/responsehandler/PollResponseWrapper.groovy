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
public class PollResponseWrapper {

    final PollResponseHandler handler
    final PollResponseDefinition definition

    public PollResponseWrapper(PollResponseHandler handler) {
        this.handler = handler
        definition = handler.getClass().getAnnotation(PollResponseDefinition.class)
    }
}
