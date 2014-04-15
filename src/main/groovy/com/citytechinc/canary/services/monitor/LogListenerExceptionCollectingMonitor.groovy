package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.google.common.collect.Lists
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Constants as OsgiConstants
import org.osgi.service.log.LogEntry
import org.osgi.service.log.LogListener
import org.osgi.service.log.LogReaderService

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@MonitoredServiceDefinition(description = 'Collects logged exceptions, reports aggregate data back when polled', pollInterval = 10, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 12)
@AutomaticResetMonitor(resetInterval = 3, resetIntervalUnit = TimeUnit.MINUTES)
class LogListenerExceptionCollectingMonitor implements MonitoredService, LogListener {

    List<LogEntry> entries = Lists.newCopyOnWriteArrayList()

    @Reference
    LogReaderService logReaderService

    @Property(name = 'scrutinizedPackagePaths', label = 'Scrutinized Packaged Paths', value = ['', ''], description = 'Package paths that should be scrutinized and reported backed to the framework')
    private List<String> scrutinizedPackagePaths

    @Property(name = 'exceptionReportingThreshold', label = 'Exception Reporting Threshold', intValue =  3, description = 'The threshold indicator used in combination with the scrutinized package paths')
    private Integer exceptionReportingThreshold

    @Activate
    protected void activate(Map<String, Object> properties) throws Exception {

        logReaderService.addLogListener(this)
        modified(properties)
    }

    @Modified
    protected void modified(Map<String, Object> properties) throws Exception {

        scrutinizedPackagePaths = PropertiesUtil.toStringArray(properties.get('scrutinizedPackagePaths')) as List
        exceptionReportingThreshold = PropertiesUtil.toInteger(properties.get('exceptionReportingThreshold'), 3)
    }

    @Deactivate
    void deactivate() {

        logReaderService.removeLogListener(this)
    }

    @Override
    PollResponse poll() {

        def messages = entries.collect { "An exception was logged at ${Constants.JMX_DATE_TIME_FORMATTER.format(it.time)}".toString() }
        entries.clear()

        messages ? PollResponse.WARNING().addMessages(messages) : PollResponse.SUCCESS()
    }

    @Override
    void logged(LogEntry logEntry) {

        if (logEntry.exception) {

            entries.add(logEntry)
        }
    }
}