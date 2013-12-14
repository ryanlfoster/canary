package com.citytechinc.monitoring.services.manager.actors.monitor

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import com.citytechinc.monitoring.services.manager.actors.missioncontrol.MissionControlActor
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

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
@Slf4j
final class MonitoredServiceActor extends DynamicDispatchActor {

    static class GetRecords {}
    static class ResumePolling {}

    MonitoredServiceWrapper wrapper
    ServiceMonitorRecordHolder recordHolder
    MissionControlActor missionControl

    TimedMonitorServiceActor timedMonitorServiceActor
    TimedMonitorSuspensionActor timedMonitorSuspensionActor

    def random = new Random()

    def startTimedMonitorServiceActor() {

        timedMonitorServiceActor = new TimedMonitorServiceActor(sleepTime: wrapper.pollIntervalInMilliseconds, monitoredService: wrapper.monitor, monitoredServiceActor: this)
        timedMonitorServiceActor.start()
    }

    def startTimedMonitorSuspensionActor() {

        timedMonitorSuspensionActor = new TimedMonitorSuspensionActor(sleepTime: 1000, monitoredServiceActor: this)
        timedMonitorSuspensionActor.start()
    }

    void afterStart() {
        startTimedMonitorServiceActor()
    }

    void onMessage(GetRecords message) {
        missionControl << recordHolder
    }

    void onMessage(ResumePolling message) {
        startTimedMonitorServiceActor()
    }

    void onMessage(DetailedPollResponse detailedPollResponse) {

        recordHolder.addRecord(detailedPollResponse)
        missionControl << detailedPollResponse

        if (random.nextBoolean()) {
            missionControl << recordHolder
        }

//        if (alarmed) {
//
//            timedMonitorServiceActor.stop()
//            startTimedMonitorSuspensionActor()
//        }
    }
}
