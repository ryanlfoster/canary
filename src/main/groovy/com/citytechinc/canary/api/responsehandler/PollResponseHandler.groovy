package com.citytechinc.canary.api.responsehandler

import com.citytechinc.canary.api.monitor.DetailedPollResponse

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface PollResponseHandler {

    /**
     *
     * @param identifer
     * @param detailedPollResponse
     */
    void handleResponse(String identifer, DetailedPollResponse detailedPollResponse)
}