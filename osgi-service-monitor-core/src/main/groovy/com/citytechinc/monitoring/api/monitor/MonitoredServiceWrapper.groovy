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
    final String monitorServiceClassName
    final MonitoredServiceDefinition definition
    final Long autoResumePollIntevalInMilliseconds
    final Long pollIntervalInMilliseconds

    public MonitoredServiceWrapper(final MonitoredService monitor) {
        this.monitor = monitor
        monitorServiceClassName = monitor.getClass().getCanonicalName()
        definition = monitor.getClass().getAnnotation(MonitoredServiceDefinition.class)
        pollIntervalInMilliseconds = TimeUnit.MILLISECONDS.convert(definition.pollInterval(), definition.pollIntervalUnit())

        final AutoResumingPoller autoResumingPollerDefinition = monitor.getClass().getAnnotation(AutoResumingPoller.class)

        if (autoResumingPollerDefinition != null) {
            autoResumePollIntevalInMilliseconds = TimeUnit.MILLISECONDS.convert(autoResumingPollerDefinition.interval(), autoResumingPollerDefinition.unit())
        } else {
            autoResumePollIntevalInMilliseconds = 0L
        }
    }
}
