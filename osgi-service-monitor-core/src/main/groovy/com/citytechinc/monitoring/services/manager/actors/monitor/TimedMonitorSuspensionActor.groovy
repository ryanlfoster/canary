package com.citytechinc.monitoring.services.manager.actors.monitor

import groovyx.gpars.actor.DefaultActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 * The purpose of this actor is to block and sleep for the configured amount of time. This
 *   blocking period of time represents
 *
 */
class TimedMonitorSuspensionActor extends DefaultActor {

    Long sleepTime
    MonitoredServiceActor monitoredServiceActor

    void act() {

        loop {

            sleep (sleepTime)
            monitoredServiceActor << new MonitoredServiceActor.ResumePolling()
            terminate()
        }
    }
}
