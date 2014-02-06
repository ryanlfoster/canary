package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import com.citytechinc.monitoring.services.manager.actors.MissionControlActor.RecordType
import com.citytechinc.monitoring.services.manager.actors.RecordPersistenceServiceActor
import com.citytechinc.monitoring.services.manager.actors.Statistics
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

    public Optional<Statistics> getStatistics(String identifier, RecordType recordType)

    public Optional<RecordHolder> getRecordHolder(String identifier)

    public void requestAllMonitorsPoll()

    public void requestAllMonitorsPersist()

    public void resetAlarm(String identifier)

    public void resetAllAlarms()

}
