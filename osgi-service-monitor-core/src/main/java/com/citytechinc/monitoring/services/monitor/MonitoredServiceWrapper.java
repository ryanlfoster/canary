package com.citytechinc.monitoring.services.monitor;

import com.citytechinc.monitoring.constants.Constants;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public final class MonitoredServiceWrapper {

    private final MonitoredService monitor;
    private final MonitoredServiceDefinition definition;
    private final AutoResumingPoller pollerDefinition;
    private final Long pollIntervalInSeconds;
    private final String schedulerKey;

    public MonitoredServiceWrapper(final MonitoredService monitor) {
        this.monitor = monitor;
        definition = monitor.getClass().getAnnotation(MonitoredServiceDefinition.class);
        pollerDefinition = monitor.getClass().getAnnotation(AutoResumingPoller.class);
        pollIntervalInSeconds = TimeUnit.SECONDS.convert(definition.pollFrequency(), definition.pollFrequencyUnit());
        schedulerKey = Constants.FAST_HASH.hashUnencodedChars(monitor.getClass().getCanonicalName()).toString();
    }

    public MonitoredServiceDefinition getDefinition() {
        return definition;
    }

    public MonitoredService getMonitor() {
        return monitor;
    }

    public AutoResumingPoller getPollerDefinition() {
        return pollerDefinition;
    }

    public Long getPollIntervalInSeconds() {
        return pollIntervalInSeconds;
    }

    public String getSchedulerKey() {
        return schedulerKey;
    }
}
