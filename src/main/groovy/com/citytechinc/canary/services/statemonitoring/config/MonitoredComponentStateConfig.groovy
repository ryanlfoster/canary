package com.citytechinc.canary.services.statemonitoring.config

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
public interface MonitoredComponentStateConfig {

    /**
     *
     * @return The component name that should be monitored.
     */
    String getMonitoredComponentName()

    /**
     *
     * @return The alarm timeout value for a component in seconds.
     */
    Integer getMonitoredComponentAlarmTimeout()
}