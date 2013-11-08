package com.citytechinc.monitoring.services.monitor

import com.citytechinc.monitoring.services.manager.MissionControlActor
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import groovyx.gpars.actor.DefaultActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
final class MonitoredServiceActor extends DefaultActor {

    MonitoredServiceWrapper wrapper
    ServiceMonitorRecordHolder recordHolder
    MissionControlActor missionControl

    void act() {

        loop {



            sleep(wrapper.pollIntervalInMilliseconds)

            wrapper.monitor.poll()
            record
        }
    }
}
