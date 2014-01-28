package com.citytechinc.monitoring.api.notification

import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 * A notification agent is passed the entire data set for a canonicalMonitorName when a condition is met. The service framework
 *   garauntees that
 *
 */
public interface NotificationAgent {

    /**
     *
     * @param record
     */
    public void notify(List<RecordHolder> recordHolders)
}
