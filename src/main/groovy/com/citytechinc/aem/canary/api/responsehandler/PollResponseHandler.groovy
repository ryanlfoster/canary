package com.citytechinc.aem.canary.api.responsehandler

import com.citytechinc.aem.canary.api.monitor.PollResult

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
     * @param identifier
     * @param pollResult
     */
    void handleResponse(String identifier, PollResult pollResult)
}