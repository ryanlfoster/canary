package com.citytechinc.canary.api.monitor

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
    final AutoResumingPoller autoResumingPollerDefinition
    final Long autoResumePollIntevalInMilliseconds
    final Long pollIntervalInSeconds
    final Long pollMaxExecutionTimeInMillseconds

    public MonitoredServiceWrapper(final MonitoredService monitor) {
        this.monitor = monitor
        canonicalMonitorName = monitor.class.canonicalName
        definition = monitor.class.getAnnotation(MonitoredServiceDefinition)
        pollIntervalInSeconds = TimeUnit.SECONDS.convert(definition.pollInterval(), definition.pollIntervalUnit())
        pollMaxExecutionTimeInMillseconds = TimeUnit.MILLISECONDS.convert(definition.pollMaxExecutionTimeInSeconds(), TimeUnit.SECONDS)

        autoResumingPollerDefinition = monitor.class.getAnnotation(AutoResumingPoller)

        if (autoResumingPollerDefinition != null) {
            autoResumePollIntevalInMilliseconds = TimeUnit.MILLISECONDS.convert(autoResumingPollerDefinition.interval(), autoResumingPollerDefinition.unit())
        } else {
            autoResumePollIntevalInMilliseconds = 0L
        }
    }
}
