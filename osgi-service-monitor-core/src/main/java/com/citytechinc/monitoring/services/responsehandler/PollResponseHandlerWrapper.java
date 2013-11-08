package com.citytechinc.monitoring.services.responsehandler;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public final class PollResponseHandlerWrapper {

    private final PollResponseHandler handler;
    private final PollResponseHandlerDefinition definition;

    public PollResponseHandlerWrapper(final PollResponseHandler handler) {
        this.handler = handler;
        this.definition = handler.getClass().getAnnotation(PollResponseHandlerDefinition.class);
    }

    public PollResponseHandlerDefinition getDefinition() {
        return definition;
    }

    public PollResponseHandler getHandler() {
        return handler;
    }
}
