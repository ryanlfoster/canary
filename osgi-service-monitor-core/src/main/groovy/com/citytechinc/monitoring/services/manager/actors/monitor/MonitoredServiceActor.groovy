package com.citytechinc.monitoring.services.manager.actors.monitor

import com.citytechinc.monitoring.api.monitor.PollResponse
import com.citytechinc.monitoring.api.persistence.RecordPersistenceService
import com.citytechinc.monitoring.services.jcrpersistence.ServiceMonitorRecord
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.services.manager.actors.MissionControlActor
import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.actor.DynamicDispatchActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
final class MonitoredServiceActor extends DynamicDispatchActor {

    MonitoredServiceWrapper wrapper
    ServiceMonitorRecordHolder recordHolder
    MissionControlActor missionControl

    void onMessage(ServiceMonitorRecord record) {
        recordHolder.addRecord(record)
    }

    void onMessage(GetRecordHolder recordPersistenceService) {
        reply recordHolder
    }
}
