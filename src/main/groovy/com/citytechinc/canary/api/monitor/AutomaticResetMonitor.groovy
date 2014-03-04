package com.citytechinc.canary.api.monitor

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
public @interface AutomaticResetMonitor {

    /**
     *
     * Service should auto-resume polling after this defined time period. If a subsequent alarm is triggered,
     *   the framework will wait for this defined period before polling again.
     *
     * @return
     */
    int resetInterval()

    /**
     *
     * Unit relating to polling period.
     *
     * @return
     */
    TimeUnit resetIntervalUnit()

}