package com.citytechinc.monitoring.api.responsehandler

import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse

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
     * @param serviceMonitorRecord
     */
    void handleResponse(DetailedPollResponse serviceMonitorRecord)
}