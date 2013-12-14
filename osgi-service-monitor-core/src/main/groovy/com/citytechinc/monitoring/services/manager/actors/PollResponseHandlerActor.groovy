package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import groovyx.gpars.actor.DynamicDispatchActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
final class PollResponseHandlerActor extends DynamicDispatchActor {

    PollResponseHandler handler

    void onMessage(DetailedPollResponse serviceMonitorRecord) {

        handler.handleResponse(serviceMonitorRecord)
    }
}
