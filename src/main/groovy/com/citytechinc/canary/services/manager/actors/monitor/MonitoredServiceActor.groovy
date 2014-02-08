package com.citytechinc.canary.services.manager.actors.monitor

import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.monitor.PollResponse
import com.citytechinc.canary.api.monitor.DetailedPollResponse
import com.citytechinc.canary.services.manager.actors.MissionControlActor
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

    MonitoredServiceWrapper wrapper
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

        log.debug("Received message to auto resume polling. Scheduling normal polling...")
        schedulePolling()
    }

    void onMessage(Poll message) {

        def startTime = new Date()
        PollResponse pollResponse = pollingActor.sendAndWait(new Poll(), wrapper.pollMaxExecutionTimeInMillseconds, TimeUnit.MILLISECONDS) ?: PollResponse.INTERRUPTED()

        def detailedPollResponse = new DetailedPollResponse(startTime: startTime,
                endTime: new Date(),
                responseType: pollResponse.pollResponseType,
                stackTrace: pollResponse.exceptionStackTrace)

        missionControl << new MissionControlActor.PollResponseReceipt(detailedPollResponse: detailedPollResponse, identifier: wrapper.canonicalMonitorName)
    }

    def schedulePolling = {

        log.debug("Adding scheduled job defined under the key: ${schedulerJobKey()}")

        scheduler.addPeriodicJob(schedulerJobKey(), {

            this << new Poll()

        }, [:], wrapper.pollIntervalInSeconds, false)
    }

    def unschedulePolling = {

        log.debug("Removing scheduled job for key ${schedulerJobKey()}")
        scheduler.removeJob(schedulerJobKey())
    }

    def schedulerJobKey = {
        jobprefix + wrapper.canonicalMonitorName
    }

    def oneTimeScheduleAutoResume = {

        if (recordHolder.isAlarmed() && wrapper.autoResumePollIntevalInMilliseconds > 0L) {

            log.debug("Adding scheduled auto resume job defined under the key: ${schedulerJobKey()}")

            final Date now = new Date()
            scheduler.fireJobAt(schedulerJobKey(), {

                this << new AutoResumePolling()

            }, [:], new Date(now.time + wrapper.autoResumePollIntevalInMilliseconds))
        }
    }
}
