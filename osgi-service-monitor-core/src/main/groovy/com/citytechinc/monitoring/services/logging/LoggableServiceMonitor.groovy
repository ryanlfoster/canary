package com.citytechinc.monitoring.services.logging

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public @interface LoggableServiceMonitor {

    /**
     *
     * The prefix
     *
     * @return
     */
    String prefix()

    /**
     *
     * A
     *
     * @return
     */
    boolean cleanupLoggerForResolvedAlarm() default true
}