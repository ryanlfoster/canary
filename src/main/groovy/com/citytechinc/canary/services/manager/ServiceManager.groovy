package com.citytechinc.canary.services.manager

import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.canary.api.responsehandler.PollResponseHandlerWrapper
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

    public List<MonitoredServiceWrapper> getMonitoredServicesConfigurations()

    public List<NotificationAgentWrapper> getNotificationAgentsConfigurations()

    public List<PollResponseHandlerWrapper> getPollResponseHandlersConfigurations()

    public List<RecordPersistenceServiceWrapper> getRecordPersistenceServicesConfigurations()

    public Optional<Statistics> getStatistics(String identifier, MissionControlActor.GetStatistics.Type type)

    public Optional<RecordHolder> getRecordHolder(String identifier)

    public void requestAllMonitorsPoll()

    public void requestAllMonitorsPersist()

    public void resetAlarm(String identifier)

    public void resetAllAlarms()

}
