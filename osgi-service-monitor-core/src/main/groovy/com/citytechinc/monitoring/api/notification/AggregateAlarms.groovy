package com.citytechinc.monitoring.api.notification

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateAlarms {

    /**
     *
     * The poll frequency for the service.
     *
     * @return
     */
    int aggregationWindow()

    /**
     *
     * @return
     */
    TimeUnit aggregationWindowTimeUnit()

}