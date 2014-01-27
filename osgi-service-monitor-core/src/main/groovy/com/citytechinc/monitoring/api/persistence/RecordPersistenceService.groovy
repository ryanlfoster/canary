package com.citytechinc.monitoring.api.persistence

import com.citytechinc.monitoring.services.jcrpersistence.ServiceMonitorRecordHolder
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
    void persistRecordHolder(ServiceMonitorRecordHolder recordHolder)

    /**
     *
     * @param monitorClass
     * @return
     */
    Optional<ServiceMonitorRecordHolder> getRecordHolder(String monitorClass)
}