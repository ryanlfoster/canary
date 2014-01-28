package com.citytechinc.monitoring.services.manager.actors.monitor

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.monitor.PollResponse
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import com.citytechinc.monitoring.services.manager.actors.MissionControlActor
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor
import org.apache.sling.commons.scheduler.Scheduler

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Slf4j
final class MonitoredServiceActor extends DynamicDispatchActor {

    static String jobprefix = 'scheduled-monitor-'

    // MESSAGES
    static class Poll {}
    static class AutoResumePolling {}
    static class ResetAlarm {}
    static class GetRecords {}

    static class BroadcastAlarm {
        RecordHolder recordHolder
        Boolean persistImmediately
    }
    static class BroadcastPollResponse {
        DetailedPollResponse pollResponse
        String canonicalMonitorName
    }

    MonitoredServiceWrapper wrapper
    RecordHolder recordHolder
    Scheduler scheduler
    MissionControlActor missionControl
    PollingActor pollingActor

    void afterStart() {
        schedulePolling()

        pollingActor = new PollingActor(service: wrapper.monitor)
        pollingActor.start()
    }

    void afterStop() {
        unschedulePolling()

        pollingActor.terminate()
    }


    void onMessage(AutoResumePolling message) {

        log.info("Received message to auto resume polling. Scheduling normal polling...")
        schedulePolling()
    }

    void onMessage(GetRecords message) {
        sender.send(recordHolder)
    }

    void onMessage(ResetAlarm message) {

        if (recordHolder.isAlarmed()) {

            recordHolder.clearAlarm()
            schedulePolling()

        } else {

            log.warn("Reset alarm received but not in alarm state")
        }
    }

    void onMessage(Poll message) {

        final Date startTime = new Date()
        final PollResponse pollResponse = pollingActor.sendAndWait(new Poll(), wrapper.pollMaxExecutionTimeInMillseconds, TimeUnit.MILLISECONDS) ?: PollResponse.INTERRUPTED()

        DetailedPollResponse detailedPollResponse = new DetailedPollResponse(startTime: startTime,
                endTime: new Date(),
                responseType: pollResponse.pollResponseType,
                stackTrace: pollResponse.exceptionStackTrace)

        // ADD RECORD TO HOLDER, SEND MESSAGE TO MISSION CONTROL WITH RESPONSE FOR BROADCAST
        recordHolder.addRecord(detailedPollResponse)
        missionControl << new BroadcastPollResponse(pollResponse: detailedPollResponse, canonicalMonitorName: recordHolder.canonicalMonitorName)

        if (recordHolder.isAlarmed()) {

            // SEND RECORDS TO MISSION CONTROL FOR BROADCAST TO PERSISTENCE SERVICES
            missionControl << new BroadcastAlarm(recordHolder: recordHolder, persistImmediately: wrapper.definition.persistWhenAlarmed())

            // REMOVE JOB SCHEDULER
            unschedulePolling()

            // SCHEDULE AUTO RESUME POLLING
            oneTimeScheduleAutoResume()
        }
    }

    def schedulePolling = {

        log.info("Adding scheduled job defined under the key: ${schedulerJobKey()}")

        scheduler.addPeriodicJob(schedulerJobKey(), {

            this << new Poll()

        }, [:], wrapper.pollIntervalInSeconds, false)
    }

    def unschedulePolling = {

        log.info("Removing scheduled job for key ${schedulerJobKey()}")
        scheduler.removeJob(schedulerJobKey())
    }

    def schedulerJobKey = {
        jobprefix + wrapper.canonicalMonitorName
    }

    def oneTimeScheduleAutoResume = {

        if (recordHolder.isAlarmed() && wrapper.autoResumePollIntevalInMilliseconds > 0L) {

            log.info("Adding scheduled auto resume job defined under the key: ${schedulerJobKey()}")

            final Date now = new Date()
            scheduler.fireJobAt(schedulerJobKey(), {

                this << new AutoResumePolling()

            }, [:], new Date(now.time + wrapper.autoResumePollIntevalInMilliseconds))
        }
    }
}
