package com.citytechinc.monitoring.services.notification

import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface NotificationAgent {

    /**
     *
     * @param record
     */
    public void notify(List<ServiceMonitorRecordHolder> recordHolders)
}
