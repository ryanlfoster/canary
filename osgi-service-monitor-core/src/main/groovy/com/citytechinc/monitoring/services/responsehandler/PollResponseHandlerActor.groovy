package com.citytechinc.monitoring.services.responsehandler

import com.citytechinc.monitoring.services.monitor.PollResponse
import groovyx.gpars.actor.DefaultActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
final class PollResponseHandlerActor extends DefaultActor {

    PollResponseHandler handler

    void act() {

        loop {

            react { String monitorName, PollResponse response ->

                handler.handleResponse(monitorName, response)
            }
        }
    }
}
