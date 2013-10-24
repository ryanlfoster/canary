package com.citytechinc.monitoring.services.monitor

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@interface MonitoredServiceDefinition {

    /**
     *
     * The poll frequency for the service.
     *
     * @return
     */
    int pollFrequency()

    /**
     *
     * @return
     */
    TimeUnit pollFrequencyUnit()

    /**
     *
     * The number of sequential failures required to trigger an alarm and suspend monitoring.
     *
     * @return
     */
    int sequentialFailedPollsToTriggerAlarm() default 3

    /**
     *
     * Service should auto-resume polling after an alarm is triggered and not reset.
     *
     * @return
     */
    boolean autoResumePolling() default false

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

    /**
     *
     * Monitor service for unintended shutdowns due to dependency failures, inactive bundles, etc...
     *
     * @return
     */
    boolean monitorForUnintendedShutdowns() default true

    /**
     *
     * The number of poll results to store in memory and in the JCR.
     *
     * @return
     */
    int pollHistoryLength() default 50

    /**
     *
     * @return
     */
    boolean createLoggerForAlarm() default true

}
