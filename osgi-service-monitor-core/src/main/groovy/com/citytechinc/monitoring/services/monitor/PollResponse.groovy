package com.citytechinc.monitoring.services.monitor

import org.apache.commons.lang.exception.ExceptionUtils

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class PollResponse {

    private final PollResponseType pollResponseType
    private final String exceptionStackTrace

    protected PollResponse(final PollResponseType pollResponseType, final String exceptionStackTrace) {
        this.pollResponseType = pollResponseType
        this.exceptionStackTrace = exceptionStackTrace
    }

    public static PollResponse SUCCESS() {
        return new PollResponse(PollResponseType.success, '')
    }

    public static PollResponse SERVICE_UNAVAILABLE() {
        return new PollResponse(PollResponseType.service_unavailable, '')
    }

    public static PollResponse UNEXPECTED_SERVICE_RESPONSE() {
        return new PollResponse(PollResponseType.unexpected_service_response, '')
    }

    public static PollResponse EXCEPTION(final Exception exception) {
        return new PollResponse(PollResponseType.exception, ExceptionUtils.getStackTrace(exception))
    }

    public PollResponseType getPollResponseType() {
        return pollResponseType
    }

    public String getExceptionStackTrace() {
        return exceptionStackTrace
    }
}
