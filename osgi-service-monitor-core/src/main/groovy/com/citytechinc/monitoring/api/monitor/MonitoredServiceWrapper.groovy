package com.citytechinc.monitoring.api.monitor

import groovy.transform.EqualsAndHashCode

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@EqualsAndHashCode
public final class MonitoredServiceWrapper {

    final MonitoredService monitor
    final String canonicalMonitorName
    final MonitoredServiceDefinition definition
    final Long autoResumePollIntevalInSeconds
    final Long pollIntervalInSeconds

    public MonitoredServiceWrapper(final MonitoredService monitor) {
        this.monitor = monitor
        canonicalMonitorName = monitor.class.canonicalName
        definition = monitor.class.getAnnotation(MonitoredServiceDefinition)
        pollIntervalInSeconds = TimeUnit.SECONDS.convert(definition.pollInterval(), definition.pollIntervalUnit())

        final AutoResumingPoller autoResumingPollerDefinition = monitor.class.getAnnotation(AutoResumingPoller)

        if (autoResumingPollerDefinition != null) {
            autoResumePollIntevalInSeconds = TimeUnit.SECONDS.convert(autoResumingPollerDefinition.interval(), autoResumingPollerDefinition.unit())
        } else {
            autoResumePollIntevalInSeconds = 0L
        }
    }
}
