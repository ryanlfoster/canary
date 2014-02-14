package com.citytechinc.canary.api.persistence

import com.citytechinc.canary.api.monitor.DetailedPollResponse
import com.citytechinc.canary.api.monitor.RecordHolder
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
    void persistRecordHolder(RecordHolder recordHolder)

    /**
     *
     * @param monitorName
     * @return
     */
    Optional<List<DetailedPollResponse>> getPollResponseRecordsForMonitor(String monitorName)
}