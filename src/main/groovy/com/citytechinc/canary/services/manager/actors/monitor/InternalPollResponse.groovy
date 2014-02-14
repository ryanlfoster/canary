package com.citytechinc.canary.services.manager.actors.monitor

import com.citytechinc.canary.api.monitor.PollResponse
import com.citytechinc.canary.api.monitor.PollResponseType
import org.apache.commons.lang.exception.ExceptionUtils

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
class InternalPollResponse extends PollResponse {

    InternalPollResponse() {
        super()
    }

    /**
     *
     * This is used to track and log exceptions.
     *
     * @param exception
     * @return
     */
    public static PollResponse EXCEPTION(final Exception exception) {
        return new PollResponse(PollResponseType.EXCEPTION, ExceptionUtils.getStackTrace(exception));
    }

    /**
     *
     * This is used to track interrupted polls, polls that have exceeded their maximum allowed process time.
     *
     * @return
     */
    public static PollResponse INTERRUPTED() {
        return new PollResponse(PollResponseType.INTERRUPTED, '');
    }
}
