package com.citytechinc.canary.api.notification

import com.citytechinc.canary.api.monitor.RecordHolder

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 * A notification agent is passed the entire data set for a identifier when a condition is met. The service framework
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
