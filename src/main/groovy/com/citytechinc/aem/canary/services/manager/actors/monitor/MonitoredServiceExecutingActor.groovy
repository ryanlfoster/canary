package com.citytechinc.aem.canary.services.manager.actors.monitor

import com.citytechinc.aem.canary.api.monitor.MonitoredServiceWrapper
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
class MonitoredServiceExecutingActor extends DynamicDispatchActor {

    MonitoredServiceWrapper wrapper

    void onMessage(MonitoredServiceActor.Poll message) {

        log.debug("Polling for monitored service: ${wrapper.identifier}")

        try {

            sender.send(wrapper.poll())

        } catch (Exception e) {

            log.error("An exception occurred while calling the monitored service: ${wrapper.identifier}. Recording exception...", e)
            sender.send(InternalPollResponse.EXCEPTION(e))
        }
    }
}
