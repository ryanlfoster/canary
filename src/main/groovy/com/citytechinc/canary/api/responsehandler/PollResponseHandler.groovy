package com.citytechinc.canary.api.responsehandler

import com.citytechinc.canary.services.persistence.DetailedPollResponse

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
    void handleResponse(String canonicalMonitorName, DetailedPollResponse serviceMonitorRecord)
}