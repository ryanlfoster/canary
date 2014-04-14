package com.citytechinc.canary.services.manager

import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.canary.api.responsehandler.PollResponseHandlerWrapper
import com.citytechinc.canary.api.monitor.MonitorRecords
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

    /**
     *
     * @return
     */
    public List<MonitoredServiceWrapper> getMonitoredServicesConfigurations()

    /**
     *
     * @return
     */
    public List<NotificationAgentWrapper> getNotificationAgentsConfigurations()

    /**
     *
     * @return
     */
    public List<PollResponseHandlerWrapper> getPollResponseHandlersConfigurations()

    /**
     *
     * @return
     */
    public List<RecordPersistenceServiceWrapper> getRecordPersistenceServicesConfigurations()

    /**
     *
     * @param identifier
     * @param type
     * @return
     */
    public Optional<Statistics> getStatistics(String identifier, MissionControlActor.GetStatistics.Type type)

    /**
     *
     * @param identifier
     * @return
     */
    public Optional<MonitorRecords> getRecordHolder(String identifier)

    /**
     *
     */
    public void requestAllMonitorsPoll()

    /**
     *
     */
    public void requestAllMonitorsPersist()

    /**
     *
     * @param identifier
     */
    public void resetAlarm(String identifier)

    /**
     *
     */
    public void resetAllAlarms()

}
