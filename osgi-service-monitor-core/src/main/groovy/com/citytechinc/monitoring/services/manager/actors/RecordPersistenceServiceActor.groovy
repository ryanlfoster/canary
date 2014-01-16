package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import groovy.transform.Immutable
import groovyx.gpars.actor.DynamicDispatchActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class RecordPersistenceServiceActor extends DynamicDispatchActor {

    @Immutable
    static class GetRecord { String monitor }

    @Immutable
    static class PersistRecord { ServiceMonitorRecordHolder recordHolder }

    RecordPersistenceServiceWrapper wrapper

    void onMessage(GetRecord message) {

        sender.send(wrapper.service.getRecordHolder(message.monitor))
    }

    void onMessage(PersistRecord message) {
        sender.send(wrapper.service.getRecordHolder(message.recordHolder))
    }
}

