package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder

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
    public List<String> listAlarmedMonitors()

    /**
     *
     * @param fullyQualifiedMonitorPath
     * @return
     */
    public RecordHolder getRecordHolder(String fullyQualifiedMonitorPath)

    /**
     *
     * @param fullyQualifiedMonitorPath
     */
    public void resetAlarm(String fullyQualifiedMonitorPath)

    /**
     *
     */
    public void resetAllAlarms()

}
