package com.citytechinc.canary.api.monitor

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
     * Used to denote that the service being polled has suddenly become unavailable. This requires optional binding to
     *   said monitored service which means that the monitor is probably not defined on said monitored service.
     *
     * @return
     */
    public static PollResponse SERVICE_UNAVAILABLE() {
        return new PollResponse(PollResponseType.SERVICE_UNAVAILABLE, '')
    }

    /**
     *
     * Unexpected response is the general response for any error or abnormal result.
     *
     * @return
     */
    public static PollResponse UNEXPECTED_SERVICE_RESPONSE() {
        return new PollResponse(PollResponseType.UNEXPECTED_SERVICE_RESPONSE, '')
    }

    public PollResponse addMessages(List<String> messages) {
        this.messages.addAll(messages)

        this
    }

    public PollResponse addMessage(String message) {
        messages.add(message)

        this
    }
}
