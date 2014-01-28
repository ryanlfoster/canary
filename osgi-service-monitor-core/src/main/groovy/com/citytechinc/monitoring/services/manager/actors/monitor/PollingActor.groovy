package com.citytechinc.monitoring.services.manager.actors.monitor

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.monitor.PollResponse
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Slf4j
class PollingActor extends DynamicDispatchActor {

    MonitoredService service

    void onMessage(MonitoredServiceActor.Poll message) {

        try {
            sender.send(service.poll())
        } catch (Exception e) {

            log.error("An exception occurred while calling the monitored service: ${service.class.canonicalName}", e)
            sender.send(PollResponse.EXCEPTION(e))
        }
    }
}
