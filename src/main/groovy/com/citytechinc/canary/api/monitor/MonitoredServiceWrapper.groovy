package com.citytechinc.canary.api.monitor

import groovy.transform.EqualsAndHashCode

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@EqualsAndHashCode
public final class MonitoredServiceWrapper {

    @Delegate final MonitoredService monitor
    final String identifier
    final MonitoredServiceDefinition definition
    final AutomaticResetMonitor automaticResetMonitorDefinition

    public MonitoredServiceWrapper(final MonitoredService monitor) {

        this.monitor = monitor
        identifier = monitor.class.canonicalName

        definition = monitor.class.getAnnotation(MonitoredServiceDefinition)
        automaticResetMonitorDefinition = monitor.class.getAnnotation(AutomaticResetMonitor)
    }
}
