package com.citytechinc.monitoring.api.responsehandler

import com.citytechinc.monitoring.api.monitor.PollResponse

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
     * @param monitorName
     * @param response
     */
    void handleResponse(String monitorName, PollResponse response)
}