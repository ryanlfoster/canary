package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder
import com.citytechinc.monitoring.services.manager.actors.monitor.Statistics

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
     */
    public void requestAllMonitorsPoll();

    /**
     *
     */
    public void requestAllMonitorsPersist();

    /**
     *
     * @return
     */
    public List<MonitoredServiceWrapper> listMonitoredServices()

    /**
     *
     * @return
     */
    public List<RecordPersistenceServiceWrapper> listRecordPersistenceServices()

    /**
     *
     * @return
     */
    public List<NotificationAgentWrapper> listNotificationAgents()

    /**
     *
     * @return
     */
    public List<PollResponseWrapper> listPollResponseHandlers()

    /**
     *
     * @return
     */
    public Statistics getPollResponseHandlerStatistics(String canonicalName)

    /**
     *
     * @return
     */
    public List<String> listAlarmedMonitors()

    /**
     *
     * @param canonicalMonitorName
     * @return
     */
    public RecordHolder getRecordHolder(String canonicalMonitorName)

    /**
     *
     * @param canonicalMonitorName
     */
    public void resetAlarm(String canonicalMonitorName)

    /**
     *
     */
    public void resetAllAlarms()

}
