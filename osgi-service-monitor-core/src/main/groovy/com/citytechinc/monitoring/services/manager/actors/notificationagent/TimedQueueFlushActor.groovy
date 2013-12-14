package com.citytechinc.monitoring.services.manager.actors.notificationagent

import groovyx.gpars.actor.DefaultActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class TimedQueueFlushActor extends DefaultActor {

    Long sleepTime
    NotificationAgentActor notificationAgentActor

    void act() {

        loop {

            sleep (sleepTime)
            notificationAgentActor << new NotificationAgentActor.FlushQueue()
            terminate()
        }
    }
}