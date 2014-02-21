package com.citytechinc.canary.api.monitor

import groovy.transform.EqualsAndHashCode
import org.apache.commons.lang.StringUtils

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
    final String name
    final MonitoredServiceDefinition definition
    final AutomaticResetMonitor automaticResetMonitorDefinition

    public MonitoredServiceWrapper(final MonitoredService monitor) {

        this.monitor = monitor
        identifier = monitor.class.canonicalName
        name = StringUtils.substringAfterLast(identifier, '.').replaceAll('(.)([A-Z])', '$1 $2')

        definition = monitor.class.getAnnotation(MonitoredServiceDefinition)
        automaticResetMonitorDefinition = monitor.class.getAnnotation(AutomaticResetMonitor)
    }
}
