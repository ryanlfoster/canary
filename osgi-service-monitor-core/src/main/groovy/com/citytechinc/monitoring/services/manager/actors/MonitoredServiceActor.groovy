package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor
import org.apache.sling.commons.scheduler.Scheduler

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

    static def jobprefix = 'scheduled-monitor-'

    // MESSAGES
    static class Poll {}
    static class AutoResumePoll {}

    MonitoredServiceWrapper wrapper
    ServiceMonitorRecordHolder recordHolder
    Scheduler scheduler
    MissionControlActor missionControl

    void afterStart() {
        schedulePolling()
    }

    void afterStop() {
        unschedulePolling()
    }

    void onMessage(AutoResumePoll message) {
        schedulePolling()
    }

    void onMessage(MissionControlActor.GetMonitorRecordHolder message) {
        sender.send(recordHolder)
    }

    void onMessage(MissionControlActor.ResetAlarm message) {

        if (recordHolder.isAlarmed()) {

            //todo reset alarm
        } else {

            log.warn("Reset alarm received but not in alarm state")
        }
    }

    void onMessage(Poll message) {

        def startTime = new Date()

        //todo wrap this call in an actor with a timeout
        def pollResponse = wrapper.monitor.poll()

        def detailedPollResponse = new DetailedPollResponse(startTime: startTime,
                endTime: new Date(),
                responseType: pollResponse.pollResponseType,
                stackTrace: pollResponse.exceptionStackTrace,
                monitoredServiceClassname: wrapper.monitor.class.name)

        // ADD RECORD TO HOLDER, SEND MESSAGE TO MISSION CONTROL WITH RESPONSE FOR BROADCAST
        recordHolder.addRecord(detailedPollResponse)
        missionControl << detailedPollResponse

        if (recordHolder.isAlarmed()) {

            // SEND RECORDS TO MISSION CONTROL FOR BROADCAST TO PERSISTENCE SERVICES
            missionControl << recordHolder

            // REMOVE JOB SCHEDULER
            unschedulePolling()

            // SCHEDULE AUTO RESUME POLLING
            oneTimeScheduleAutoResume()
        }

    }

    def schedulePolling = {

        if (!recordHolder.isAlarmed()) {

            scheduler.addPeriodicJob(schedulerJobKey(), {

                log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! JOB RAN")
                this << new Poll()

            }, [:], wrapper.pollIntervalInMilliseconds, false)

            log.info("Adding schedule job defined under the key: ${schedulerJobKey()}")
        }
    }

    def unschedulePolling = {

        log.info("Removing schedule job for key ${schedulerJobKey()}")
        scheduler.removeJob(schedulerJobKey())
    }

    def schedulerJobKey = {
        jobprefix + wrapper.monitorServiceClassName
    }

    def oneTimeScheduleAutoResume = {

        if (recordHolder.isAlarmed() && wrapper.autoResumePollIntevalInMilliseconds > 0L) {

            def now = new Date()
            scheduler.fireJobAt(schedulerJobKey(), {

                this << new AutoResumePoll()

            }, [:], new Date(now.time + wrapper.autoResumePollIntevalInMilliseconds))
        }
    }
}
