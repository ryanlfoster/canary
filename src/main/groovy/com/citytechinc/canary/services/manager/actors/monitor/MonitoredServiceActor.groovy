package com.citytechinc.canary.services.manager.actors.monitor

import com.citytechinc.canary.api.monitor.DetailedPollResponse
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.monitor.PollResponse
import com.citytechinc.canary.api.monitor.RecordHolder
import com.citytechinc.canary.services.manager.actors.MissionControlActor
import com.citytechinc.canary.services.manager.actors.responsehandler.PollResponseHandlerActor
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

    static String jobprefix = 'canarymon-'

    // MESSAGES
    static class Poll {}
    static class AutoResumePolling {}
    static class ResetAlarm { }
    static class GetRecord { }

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

        schedulePolling()
    }

    void onMessage(GetRecord message) {

        sender.send(recordHolder.clone())
    }

    void onMessage(ResetAlarm message) {

        if (recordHolder.isAlarmed()) {

            recordHolder.resetAlarm()
            schedulePolling()
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

        missionControl << new PollResponseHandlerActor.PollResponseReceipt(identifier: recordHolder.canonicalMonitorName, response: detailedPollResponse)

        if (recordHolder.isAlarmed()) {

            missionControl << recordHolder.clone()
            unschedulePolling()
            oneTimeScheduleAutoResume()
        }
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
