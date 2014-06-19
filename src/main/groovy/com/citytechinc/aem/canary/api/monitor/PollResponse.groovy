package com.citytechinc.aem.canary.api.monitor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public class PollResponse {

    private final PollResponseType pollResponseType
    private final String exceptionStackTrace
    private final List<String> messages = []

    private PollResponse(PollResponseType pollResponseType, String exceptionStackTrace) {
        this.pollResponseType = pollResponseType
        this.exceptionStackTrace = exceptionStackTrace
    }

    /**
     *
     * This should be used to denote a successful poll response. Everything other than this response is potentially
     *   considered an error.
     *
     * @return
     */
    public static PollResponse SUCCESS() {
        return new PollResponse(PollResponseType.SUCCESS, '')
    }

    /**
     *
     * @return
     */
    public static PollResponse WARNING() {
        return new PollResponse(PollResponseType.WARNING, '')
    }

    /**
     *
     * @return
     */
    public static PollResponse ERROR() {
        return new PollResponse(PollResponseType.ERROR, '')
    }

    /**
     *
     * @param messages
     * @return
     */
    public PollResponse addMessages(List<String> messages) {
        this.messages.addAll(messages)

        this
    }

    /**
     *
     * @param message
     * @return
     */
    public PollResponse addMessage(String message) {
        messages.add(message)

        this
    }
}
