package com.citytechinc.monitoring.services.notification

import com.citytechinc.monitoring.services.persistence.ServiceMonitorRecord

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
    public void notify(ServiceMonitorRecord record)
}
