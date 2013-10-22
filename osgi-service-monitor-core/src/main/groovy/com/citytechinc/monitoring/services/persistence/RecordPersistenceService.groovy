package com.citytechinc.monitoring.services.persistence

import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface RecordPersistenceService {

    /**
     *
     * Records are persisted in the structure:
     *
     * /etc/service_monitoring/$MONITOR_CLASS_PATH/$START_TIME
     *
     * @param records
     */
    void persistRecords(List<ServiceMonitorRecordHolder> records)

    /**
     *
     * @return
     */
    List<ServiceMonitorRecordHolder> loadRecords()
}