package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import groovyx.gpars.actor.DynamicDispatchActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
final class RecordPersistenceServiceActor extends DynamicDispatchActor {

    // MESSAGES
    static class GetRecord { String monitor }
    static class PersistRecord { RecordHolder recordHolder }

    RecordPersistenceServiceWrapper wrapper

    void onMessage(GetRecord message) {
        sender.send(wrapper.service.getRecordHolder(message.monitor))
    }

    void onMessage(PersistRecord message) {
        sender.send(wrapper.service.getRecordHolder(message.recordHolder))
    }
}

