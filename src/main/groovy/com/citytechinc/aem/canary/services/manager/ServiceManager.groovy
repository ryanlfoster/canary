package com.citytechinc.aem.canary.services.manager

import com.citytechinc.aem.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.aem.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.aem.canary.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.aem.canary.api.responsehandler.PollResponseHandlerWrapper
import com.citytechinc.aem.canary.api.monitor.MonitorRecords
import com.citytechinc.aem.canary.services.manager.actors.MissionControlActor
import com.citytechinc.aem.canary.services.manager.actors.Statistics
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
     * Sends synchronous (blocking) request to Mission Control, requesting the statistics for a specific
     *   Persistence Service, Notification Agent, or Poll Response Handler.
     *
     * @param identifier
     * @param type
     * @return
     */
    public Optional<Statistics> getStatistics(String identifier, MissionControlActor.GetStatistics.Type type)

    /**
     *
     * Sends synchronous (blocking) request to Mission Control, requesting the records for a specific monitor.
     *
     * @param identifier
     * @return
     */
    public Optional<MonitorRecords> getRecordHolder(String identifier)

    /**
     *
     * Sends asynchronous request to Mission Control, requesting that all monitors poll, even if they are in an alarmed
     *   state.
     *
     */
    public void requestAllMonitorsPoll()

    /**
     *
     * Sends asynchronous request to Mission Control, requesting that all monitors persist their records to all
     *   appropriate PersistenceServices.
     *
     */
    public void requestAllMonitorsPersist()

    /**
     *
     * Sends asynchronous request to Mission Control to request that the specified monitor its alarm.
     *
     * @param identifier
     */
    public void resetAlarm(String identifier)

    /**
     *
     * Sends asynchronous request to Mission Control to request all monitors reset their alarms.
     *
     */
    public void resetAllAlarms()

}
