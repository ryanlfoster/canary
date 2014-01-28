package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

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

    void onMessage(GetRecord message) {

        try {
            sender.send(wrapper.service.getRecordHolder(message.canonicalMonitorName))
        } catch (Exception e) {
            log.error("An exception occurred attempting to retrieve record holder for service: ${message.canonicalMonitorName} via persistence service: ${wrapper.service.class.canonicalName}", e)
        }
    }

    void onMessage(PersistRecord message) {

        try {
            wrapper.service.persistRecordHolder(message.recordHolder)
        } catch (Exception e) {
            log.error("An exception occurred attempting to persist record holder for service: ${message.recordHolder.canonicalMonitorName} via persistence service: ${wrapper.service.class.canonicalName}", e)
        }
    }
}

