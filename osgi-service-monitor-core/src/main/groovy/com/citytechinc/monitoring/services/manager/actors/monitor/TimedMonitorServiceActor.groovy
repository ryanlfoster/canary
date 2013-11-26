package com.citytechinc.monitoring.services.manager.actors.monitor

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.services.jcrpersistence.ServiceMonitorRecord
import groovyx.gpars.actor.DefaultActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 * The purpose of this actor is to block and sleep for the configured amount of time. When it
 *   wakes it spawns a new actor with a given execution timeout. Responses from this process - whether
 *   a timeout, exception, or general valid response are pushed back to the {@link MonitoredServiceActor}.
 *
 */
class TimedMonitorServiceActor extends DefaultActor {

    Long sleepTime
    MonitoredService monitoredService
    MonitoredServiceActor monitoredServiceActor

    void act() {

        loop {

            sleep (sleepTime)

            def startTime = new Date()
            def pollResponse = monitoredService.poll()
            monitoredServiceActor << new ServiceMonitorRecord(startTime: startTime, endTime: new Date(), responseType: pollResponse.pollResponseType, stackTrace: pollResponse.exceptionStackTrace)
        }
    }
}
