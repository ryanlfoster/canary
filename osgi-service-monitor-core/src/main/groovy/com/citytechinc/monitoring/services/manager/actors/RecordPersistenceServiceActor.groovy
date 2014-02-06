package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
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

    RecordPersistenceServiceWrapper wrapper
    MissionControlActor missionControl

    void onMessage(GetRecord message) {

        Stopwatch stopwatch = Stopwatch.createStarted()
        Boolean exceptionOccurred = false

        try {
            sender.send(wrapper.service.getRecordHolder(message.canonicalMonitorName))
        } catch (Exception e) {
            exceptionOccurred = true
            log.error("An exception occurred attempting to retrieve record holder for service: ${message.canonicalMonitorName} via persistence service: ${wrapper.service.class.canonicalName}", e)
        }

        missionControl << new MissionControlActor.InternalProcessAccounting(
                recordType: MissionControlActor.RecordType.RECORD_PERSISTENCE_SERVICE,
                processTime: stopwatch.stop().elapsed(TimeUnit.MILLISECONDS),
                identifier: wrapper.service.class.canonicalName,
                exceptionOccurred: exceptionOccurred)
    }

    void onMessage(PersistRecord message) {

        Stopwatch stopwatch = Stopwatch.createStarted()
        Boolean exceptionOccurred = false

        try {
            wrapper.service.persistRecordHolder(message.recordHolder)
        } catch (Exception e) {
            exceptionOccurred = true
            log.error("An exception occurred attempting to persist record holder for service: ${message.recordHolder.canonicalMonitorName} via persistence service: ${wrapper.service.class.canonicalName}", e)
        }

        missionControl << new MissionControlActor.InternalProcessAccounting(
                recordType: MissionControlActor.RecordType.RECORD_PERSISTENCE_SERVICE,
                processTime: stopwatch.stop().elapsed(TimeUnit.MILLISECONDS),
                identifier: wrapper.service.class.canonicalName,
                exceptionOccurred: exceptionOccurred)
    }
}

