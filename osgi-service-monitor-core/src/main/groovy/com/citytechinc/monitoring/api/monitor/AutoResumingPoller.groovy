package com.citytechinc.monitoring.api.monitor

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public @interface AutoResumingPoller {

    /**
     *
     * Service should auto-resume polling after this defined time period. If a subsequent alarm is triggered,
     *   the framework will wait for this defined period before polling again.
     *
     * @return
     */
    int autoResumePollingPeriod() default 0

    /**
     *
     * Unit relating to polling period.
     *
     * @return
     */
    TimeUnit autoResumePollingUnit()
}