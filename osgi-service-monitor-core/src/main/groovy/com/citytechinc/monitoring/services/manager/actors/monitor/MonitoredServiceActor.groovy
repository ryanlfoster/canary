package com.citytechinc.monitoring.services.manager.actors.monitor

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.services.jcrpersistence.ServiceMonitorRecord
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import com.citytechinc.monitoring.services.manager.actors.MissionControlActor
import groovyx.gpars.actor.DefaultActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 * This stateful actor contains historical information regarding poll data. It also manages two actors:
 *
 *   1. A timed actor that executes calls to the monitored service
 *   2. A timed actor that is used when an alarm is thrown and a timeout is requested
 *
 */
final class MonitoredServiceActor extends DefaultActor {

    MonitoredServiceWrapper wrapper
    ServiceMonitorRecordHolder recordHolder
    MissionControlActor missionControl

    TimedMonitorServiceActor timedMonitorServiceActor
    TimedMonitorSuspensionActor timedMonitorSuspensionActor

    void act() {

        timedMonitorServiceActor = new TimedMonitorServiceActor(sleepTime: wrapper.pollIntervalInMilliseconds, monitoredService: wrapper.monitor, monitoredServiceActor: this)
        timedMonitorServiceActor.start()

        loop {

            react { message ->

                switch (message) {

                    case "getRecords":

                        missionControl << recordHolder
                        break
                    case "resume":

                        timedMonitorServiceActor = new TimedMonitorServiceActor(sleepTime: wrapper.pollIntervalInMilliseconds, monitoredService: wrapper.monitor, monitoredServiceActor: this)
                        timedMonitorServiceActor.start()

                        break
                    case ServiceMonitorRecord:

                        recordHolder.addRecord(message)

                        if (alarmed) {

                            timedMonitorServiceActor.stop()

                            timedMonitorSuspensionActor = new TimedMonitorSuspensionActor(sleepTime: 1000, monitoredServiceActor: this)
                            timedMonitorSuspensionActor.start()
                        }

                        break
                }
            }
        }
    }

}
