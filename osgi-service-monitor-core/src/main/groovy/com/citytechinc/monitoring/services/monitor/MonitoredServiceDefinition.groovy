package com.citytechinc.monitoring.services.monitor

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
@interface MonitoredServiceDefinition {

    /**
     *
     * A sensible name for the monitor. If none is defined, the classname is used.
     *
     * @return
     */
    String name()

    /**
     *
     * A description of what is polled.
     *
     * @return
     */
    String description()

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
