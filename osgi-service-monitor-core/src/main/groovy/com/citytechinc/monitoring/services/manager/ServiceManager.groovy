package com.citytechinc.monitoring.services.manager

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
     * @param identifer
     * @return
     */
    ServiceMonitorRecordHolder getRecordHolder(String identifer)
}
