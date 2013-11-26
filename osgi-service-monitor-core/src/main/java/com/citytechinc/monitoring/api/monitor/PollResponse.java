package com.citytechinc.monitoring.api.monitor;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public class PollResponse {

    private final PollResponseType pollResponseType;
    private final String exceptionStackTrace;

    private PollResponse(final PollResponseType pollResponseType, final String exceptionStackTrace) {
        this.pollResponseType = pollResponseType;
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public static PollResponse SUCCESS() {
        return new PollResponse(PollResponseType.success, "");
    }

    public static PollResponse SERVICE_UNAVAILABLE() {
        return new PollResponse(PollResponseType.service_unavailable, "");
    }

    public static PollResponse UNEXPECTED_SERVICE_RESPONSE() {
        return new PollResponse(PollResponseType.unexpected_service_response, "");
    }

    public static PollResponse EXCEPTION(final Exception exception) {
        return new PollResponse(PollResponseType.exception, ExceptionUtils.getStackTrace(exception));
    }

    public static PollResponse INTERRUPTED() {
        return new PollResponse(PollResponseType.interrupted, "");
    }

    public PollResponseType getPollResponseType() {
        return pollResponseType;
    }

    public String getExceptionStackTrace() {
        return exceptionStackTrace;
    }
}
