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
@interface MonitoredServiceDefinition {

    /**
     *
     * A sensible name for the identifier. If none is defined, the class name is used.
     *
     * @return
     */
    String name() default ''

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
     * The poll frequency resetIntervalUnit used with the pollInterval()
     *
     * @return
     */
    TimeUnit pollIntervalUnit() default TimeUnit.SECONDS

    /**
     *
     * The maximum number of poll records to store in the transient record store.
     *
     * @return
     */
    int maxNumberOfRecords() default 50

    /**
     *
     * The alarm criteria used to determine if a monitor is in a healthy or unhealthy state.
     *
     * @return
     */
    AlarmCriteria alarmCriteria() default AlarmCriteria.RECENT_POLLS

    /**
     *
     * The alarm threshold, used with the alarmCriteria(), used to determine if a monitor is in a healthy or unhealthy state.
     *
     * @return
     */
    int alarmThreshold() default 3

    /**
     *
     * Persist when alarmed indicates to the framework that the transient state of the in-memory records be written to any registered, relevant persistence services.
     *
     * @return
     */
    boolean persistWhenAlarmed() default true

    /**
     *
     * This is the maximum time in seconds allowed for an individual poll. If this value is reached, the poll is dropped
     *   and the response is recorded as INTERRUPTED, which is considered a failure. This is represented in milliseconds.
     *
     * @return
     */
    long maxExecutionTime() default 3000L

}
