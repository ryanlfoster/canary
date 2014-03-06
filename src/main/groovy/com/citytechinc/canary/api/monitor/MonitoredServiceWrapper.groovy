package com.citytechinc.canary.api.monitor

import groovy.transform.EqualsAndHashCode
import org.apache.commons.lang.StringUtils
import org.codehaus.jackson.annotate.JsonIgnore

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

    @JsonIgnore
    @Delegate
    final MonitoredService monitor

    final String identifier

    // DEFINITION
    final String name
    final String description
    final Integer pollInterval
    final TimeUnit pollIntervalUnit
    final Integer maxNumberOfRecords
    final AlarmCriteria alarmCriteria
    final Integer alarmThreshold
    final Boolean persistWhenAlarmed
    final Long maxExecutionTime
    final Boolean escalateLogs

    // RESET MONITOR
    final Boolean resetCriteriaDefined
    final Integer resetInterval
    final TimeUnit resetIntervalUnit

    public MonitoredServiceWrapper(final MonitoredService monitor) {

        this.monitor = monitor

        def definition = monitor.class.getAnnotation(MonitoredServiceDefinition)
        def automaticResetMonitorDefinition = monitor.class.getAnnotation(AutomaticResetMonitor)

        identifier = monitor.class.canonicalName
        name = definition.name() ?: StringUtils.substringAfterLast(identifier, '.').replaceAll('(.)([A-Z])', '$1 $2')
        description = definition.description()
        pollInterval = definition.pollInterval()
        pollIntervalUnit = definition.pollIntervalUnit()
        maxNumberOfRecords = definition.maxNumberOfRecords()
        alarmCriteria = definition.alarmCriteria()
        alarmThreshold = definition.alarmThreshold()
        persistWhenAlarmed = definition.persistWhenAlarmed()
        maxExecutionTime = definition.maxExecutionTime()
        escalateLogs = definition.logEscalation()

        resetCriteriaDefined = automaticResetMonitorDefinition != null
        resetInterval = resetCriteriaDefined ? automaticResetMonitorDefinition.resetInterval() : null
        resetIntervalUnit = resetCriteriaDefined ? automaticResetMonitorDefinition.resetIntervalUnit() : null
    }
}
