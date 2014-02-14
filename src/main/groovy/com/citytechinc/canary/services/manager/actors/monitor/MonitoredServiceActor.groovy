package com.citytechinc.canary.services.manager.actors.monitor

import com.citytechinc.canary.api.monitor.DetailedPollResponse
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.monitor.PollResponse
import com.citytechinc.canary.api.monitor.PollResponseType
import com.citytechinc.canary.api.monitor.RecordHolder
import com.citytechinc.canary.services.manager.actors.MissionControlActor
import com.citytechinc.canary.services.manager.actors.PollResponseHandlerActor
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
    MonitoredServiceExecutingActor pollingActor

    void afterStart() {

        schedulePolling()

        pollingActor = new MonitoredServiceExecutingActor(wrapper: wrapper)
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

//        sender.send(recordHolder.clone())
        sender.send(recordHolder)
    }

    void onMessage(ResetAlarm message) {

        if (recordHolder.isAlarmed()) {

            recordHolder.resetAlarm()
            schedulePolling()
        }
    }

    void onMessage(Poll message) {

        final Date startTime = new Date()
        final PollResponse pollResponse = pollingActor.sendAndWait(new Poll(), wrapper.definition.maxExecutionTime(), TimeUnit.MILLISECONDS) ?: InternalPollResponse.INTERRUPTED()

        DetailedPollResponse detailedPollResponse = new DetailedPollResponse(startTime: startTime,
                endTime: new Date(),
                responseType: pollResponse.pollResponseType,
                stackTrace: pollResponse.exceptionStackTrace)

        if (pollResponse.pollResponseType == PollResponseType.INTERRUPTED) {
            log.debug("Interrupted a poll which started on ${detailedPollResponse.startTime} and exceeded the max execution time of ${wrapper.definition.maxExecutionTime()} ms")
        }

        // ADD RECORD TO HOLDER, SEND MESSAGE TO MISSION CONTROL WITH RESPONSE FOR BROADCAST
        recordHolder.addRecord(detailedPollResponse)

        missionControl << new PollResponseHandlerActor.PollResponseReceipt(identifier: recordHolder.monitorIdentifier, response: detailedPollResponse)

        if (recordHolder.isAlarmed()) {

            missionControl << recordHolder.clone()
            unschedulePolling()
            oneTimeScheduleAutoResume()
        }
    }

    def schedulePolling = {

        Long pollIntervalInSeconds = TimeUnit.SECONDS.convert(wrapper.definition.pollInterval(), wrapper.definition.pollIntervalUnit())

        log.debug("Adding scheduled job defined under the key: ${schedulerJobKey()} will fire every ${pollIntervalInSeconds} seconds")

        scheduler.addPeriodicJob(schedulerJobKey(), {

            this << new Poll()

        }, [:], pollIntervalInSeconds, false)
    }

    def unschedulePolling = {

        log.debug("Removing scheduled job for key ${schedulerJobKey()}")
        scheduler.removeJob(schedulerJobKey())
    }

    def schedulerJobKey = {

        jobprefix + wrapper.identifier
    }

    def oneTimeScheduleAutoResume = {

        if (recordHolder.isAlarmed() && wrapper.automaticResetMonitorDefinition) {

            Long automaticResetPollInterval = TimeUnit.MILLISECONDS.convert(wrapper.automaticResetMonitorDefinition.interval(), wrapper.automaticResetMonitorDefinition.unit())

            log.debug("Adding scheduled auto resume job defined under the key: ${schedulerJobKey()} will fire in ${automaticResetPollInterval} ms")

            final Date now = new Date()
            scheduler.fireJobAt(schedulerJobKey(), {

                this << new AutoResumePolling()

            }, [:], new Date(now.time + automaticResetPollInterval))
        }
    }
}
