package com.citytechinc.monitoring.services.monitor

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
     * Service should auto-remove polling after n minutes when an alarm is triggered and not reset.
     *
     * @return
     */
    int autoResumePollingPeriod() default 0

    /**
     *
     * @return
     */
    TimeUnit autoResumePollingUnit()
}