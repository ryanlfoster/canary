package com.citytechinc.canary.api.persistence

import com.citytechinc.canary.api.monitor.MonitorRecords
import com.citytechinc.canary.api.monitor.PollResult
import com.google.common.base.Optional

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
     * @param recordHolder
     */
    void persistRecordHolder(MonitorRecords recordHolder)

    /**
     *
     * @param monitorName
     * @return
     */
    Optional<List<PollResult>> getPollResponseRecordsForMonitor(String monitorName)
}