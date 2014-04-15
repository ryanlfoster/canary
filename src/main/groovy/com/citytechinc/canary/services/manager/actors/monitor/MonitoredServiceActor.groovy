package com.citytechinc.canary.services.manager.actors.monitor

import com.citytechinc.canary.api.monitor.PollResult
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.monitor.PollResponse
import com.citytechinc.canary.api.monitor.PollResponseType
import com.citytechinc.canary.api.monitor.MonitorRecords
import com.citytechinc.canary.api.notification.AlarmNotification
import com.citytechinc.canary.api.notification.AlarmResetNotification
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

    static String JOB_PREFIX = 'canarymon-'

    // MESSAGES
    static class Poll {}
    static class AutoResumePolling {}
    static class ResetAlarm { }
    static class GetRecord { }

    MonitoredServiceWrapper wrapper
    MonitorRecords recordHolder
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

        // todo clone the record holder
        sender.send(recordHolder)
    }

    void onMessage(ResetAlarm message) {

        if (recordHolder.isAlarmed()) {

            recordHolder.resetAlarm()
            schedulePolling()
            missionControl << new AlarmResetNotification(monitorName: wrapper.name, monitorDescription: wrapper.description, recordHolder: recordHolder)
        }
    }

    void onMessage(Poll message) {

        final Date startTime = new Date()
        final PollResponse pollResponse = pollingActor.sendAndWait(new Poll(), wrapper.maxExecutionTime, TimeUnit.MILLISECONDS) ?: InternalPollResponse.INTERRUPTED()

        PollResult pollResult = new PollResult(startTime: startTime,
                endTime: new Date(),
                responseType: pollResponse.pollResponseType,
                messages: pollResponse.messages,
                stackTrace: pollResponse.exceptionStackTrace)

        if (pollResponse.pollResponseType == PollResponseType.INTERRUPTED) {
            log.debug("Interrupted monitor ${wrapper.identifier} started on ${pollResult.startTime} and exceeded ${wrapper.maxExecutionTime}ms")
        }

        // ADD RECORD TO HOLDER, SEND MESSAGE TO MISSION CONTROL WITH RESPONSE FOR BROADCAST
        Boolean isAlarmed = recordHolder.addRecord(pollResult)

        missionControl << new PollResponseHandlerActor.PollResponseReceipt(identifier: recordHolder.monitorIdentifier, response: pollResult.clone())

        if (isAlarmed) {

            // todo clone the record holder
            missionControl << new AlarmNotification(monitorName: wrapper.name, monitorDescription: wrapper.description, recordHolder: recordHolder)
            unschedulePolling()
            oneTimeScheduleAutoResume()
        }
    }

    def schedulePolling = {

        Long pollIntervalInSeconds = TimeUnit.SECONDS.convert(wrapper.pollInterval, wrapper.pollIntervalUnit)

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

        JOB_PREFIX + wrapper.identifier
    }

    def oneTimeScheduleAutoResume = {

        if (recordHolder.isAlarmed() && wrapper.resetCriteriaDefined) {

            Long automaticResetPollInterval = TimeUnit.MILLISECONDS.convert(wrapper.getResetInterval(), wrapper.getResetIntervalUnit())

            log.debug("Adding scheduled auto resume job defined under the key: ${schedulerJobKey()} will fire in ${automaticResetPollInterval} ms")

            final Date now = new Date()
            scheduler.fireJobAt(schedulerJobKey(), {

                this << new AutoResumePolling()

            }, [:], new Date(now.time + automaticResetPollInterval))
        }
    }
}
