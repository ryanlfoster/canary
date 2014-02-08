package com.citytechinc.canary.services.manager

import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.canary.api.responsehandler.PollResponseWrapper
import com.citytechinc.canary.api.monitor.RecordHolder
import com.citytechinc.canary.services.manager.actors.MissionControlActor
import com.citytechinc.canary.services.manager.actors.Statistics
import com.google.common.base.Optional

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface ServiceManager {

    public List<MonitoredServiceWrapper> getMonitoredServices()

    public List<NotificationAgentWrapper> getNotificationAgents()

    public List<PollResponseWrapper> getPollResponseHandlers()

    public List<RecordPersistenceServiceWrapper> getRecordPersistenceServices()

    public Optional<Statistics> getStatistics(String identifier, MissionControlActor.RecordType recordType)

    public Optional<RecordHolder> getRecordHolder(String identifier)

    public void requestAllMonitorsPoll()

    public void requestAllMonitorsPersist()

    public void resetAlarm(String identifier)

    public void resetAllAlarms()

}
