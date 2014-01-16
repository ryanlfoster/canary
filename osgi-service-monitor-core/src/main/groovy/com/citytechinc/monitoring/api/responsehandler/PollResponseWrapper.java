package com.citytechinc.monitoring.api.responsehandler;

/**
 *
 * @author Josh Durbin
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
public class PollResponseWrapper {

    private final PollResponseHandler handler;
    private final PollResponseDefinition definition;

    public PollResponseWrapper(PollResponseHandler handler) {
        this.handler = handler;
        definition = handler.getClass().getAnnotation(PollResponseDefinition.class);
    }

    public PollResponseHandler getHandler() {
        return handler;
    }

    public PollResponseDefinition getDefinition() {
        return definition;
    }
}
