package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.services.monitor.MonitoredServiceDefinition
import com.citytechinc.monitoring.services.notification.NotificationAgentDefinition
import com.citytechinc.monitoring.services.persistence.RecordPersistenceService

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface ServiceManager {

    void forcePoll()

    List<MonitoredServiceDefinition> listRegisteredMonitoredServices()

    List<NotificationAgentDefinition> listRegisteredNotificationAgents()

    List<RecordPersistenceService> listRegisteredRecordPersistenceManagers()

}
