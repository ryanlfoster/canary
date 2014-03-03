package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.google.common.collect.Lists
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
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
@MonitoredServiceDefinition(description = 'Collects logged exceptions, reports aggregate data back when polled', pollInterval = 3, pollIntervalUnit = TimeUnit.MINUTES, alarmThreshold = 10)
class LogListenerExceptionCollectingMonitor implements MonitoredService, LogListener {

    List<LogEntry> entries = Lists.newCopyOnWriteArrayList()

    @Reference
    LogReaderService logReaderService

    @Activate
    void activate() {

        logReaderService.addLogListener(this)
    }

    @Deactivate
    void deactivate() {

        logReaderService.removeLogListener(this)
    }

    @Override
    PollResponse poll() {
        return null
    }

    @Override
    void logged(LogEntry logEntry) {

        if (logEntry.exception) {

            entries.add(logEntry)
        }
    }
}