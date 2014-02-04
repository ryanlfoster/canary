package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import com.citytechinc.monitoring.services.manager.actors.monitor.Statistics
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
    static class GetRecord { String canonicalMonitorName }
    static class PersistRecord { RecordHolder recordHolder }
    static class GetStatistics { String canonicalPersistenceHandler }

    RecordPersistenceServiceWrapper wrapper
    Statistics statistics = new Statistics()

    void onMessage(GetStatistics message) {

        log.info("Received statistics request for ${wrapper.class.canonicalName}")

        sender.send(statistics.clone())
    }

    void onMessage(GetRecord message) {

        Stopwatch stopwatch = Stopwatch.createStarted()

        try {
            ++statistics.numberOfProcessedMessages
            sender.send(wrapper.service.getRecordHolder(message.canonicalMonitorName))
        } catch (Exception e) {
            ++statistics.numberOfMessageExceptions
            log.error("An exception occurred attempting to retrieve record holder for service: ${message.canonicalMonitorName} via persistence service: ${wrapper.service.class.canonicalName}", e)
        }

        stopwatch.stop()
        statistics.addProcessTime(stopwatch.elapsed(TimeUnit.MILLISECONDS))
    }

    void onMessage(PersistRecord message) {

        Stopwatch stopwatch = Stopwatch.createStarted()

        try {
            ++statistics.numberOfProcessedMessages
            wrapper.service.persistRecordHolder(message.recordHolder)
        } catch (Exception e) {
            ++statistics.numberOfMessageExceptions
            log.error("An exception occurred attempting to persist record holder for service: ${message.recordHolder.canonicalMonitorName} via persistence service: ${wrapper.service.class.canonicalName}", e)
        }

        stopwatch.stop()
        statistics.addProcessTime(stopwatch.elapsed(TimeUnit.MILLISECONDS))
    }
}

