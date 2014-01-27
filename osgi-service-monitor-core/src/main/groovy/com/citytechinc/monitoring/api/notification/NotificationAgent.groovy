package com.citytechinc.monitoring.api.notification

import com.citytechinc.monitoring.services.jcrpersistence.ServiceMonitorRecordHolder

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 * A notification agent is passed the entire data set for a monitor when a condition is met. The service framework
 *   garauntees that
 *
 */
public interface NotificationAgent {

    /**
     *
     * @param record
     */
    public void notify(List<ServiceMonitorRecordHolder> recordHolders)
}
