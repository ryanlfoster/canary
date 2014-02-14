package com.citytechinc.canary.services.manager.actors

import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.canary.api.monitor.RecordHolder
import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Slf4j
final class RecordPersistenceServiceActor extends DynamicDispatchActor {

    // MESSAGES
    static class GetPersistedRecord {
        String identifier
    }

    static class PersistRecord { RecordHolder recordHolder }

    RecordPersistenceServiceWrapper wrapper
    Statistics statistics = new Statistics()

    void onMessage(MissionControlActor.GetStatistics message) {
        sender.send(statistics.clone())
    }

    void onMessage(GetPersistedRecord message) {

        ++statistics.deliveredMessages

        Stopwatch stopwatch = Stopwatch.createStarted()

        try {

            sender.send(wrapper.getPollResponseRecordsForMonitor(message.identifier))
            ++statistics.processedMessages

        } catch (Exception e) {

            log.error("An exception occurred attempting to retrieve record holder for service: ${message.identifier} via persistence service: ${wrapper.identifier}", e)
            ++statistics.messageExceptions
        }

        Long processTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)
        log.debug("It took ${processTime} ms to retrieve record holder for service ${message.identifier} via persistence service ${wrapper.identifier}")

        statistics.addAndCalculateAverageProcessTime(processTime)
    }

    void onMessage(PersistRecord message) {

        ++statistics.deliveredMessages

        Stopwatch stopwatch = Stopwatch.createStarted()

        try {

            wrapper.persistRecordHolder(message.recordHolder)
            ++statistics.processedMessages

        } catch (Exception e) {

            log.error("An exception occurred attempting to persist record holder for service: ${message.recordHolder.monitorIdentifier} via persistence service: ${wrapper.identifier}", e)
            ++statistics.messageExceptions
        }

        Long processTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)
        log.debug("It took ${processTime} ms to persist record holder for service ${message.recordHolder.monitorIdentifier} via persistence service ${wrapper.identifier}")

        statistics.addAndCalculateAverageProcessTime(processTime)
    }
}

