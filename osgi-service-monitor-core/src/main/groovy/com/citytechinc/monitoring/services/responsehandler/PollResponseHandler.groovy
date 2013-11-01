package com.citytechinc.monitoring.services.responsehandler

import com.citytechinc.monitoring.services.monitor.PollResponse

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