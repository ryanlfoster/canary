package com.citytechinc.monitoring.api.monitor

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
     * A sensible name for the canonicalMonitorName. If none is defined, the class name is used.
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
    String description() default ''

    /**
     *
     * The poll frequency for the service.
     *
     * @return
     */
    int pollInterval()

    /**
     *
     * @return
     */
    TimeUnit pollIntervalUnit()

    /**
     *
     * The number of sequential failures required to trigger an alarm and suspend monitoring.
     *
     * @return
     */
    int alarmThreshold() default 3

    /**
     *
     * The number of poll results to store in memory and in the JCR.
     *
     * @return
     */
    int maxPollHistoryEntries() default 50

    /**
     *
     * This flag indicates that any canonicalMonitorName that goes into an 'alarmed' state will request persistence.
     *
     * @return
     */
    boolean persistWhenAlarmed() default false

    int pollMaxExecutionTimeInSeconds() default 3

}
