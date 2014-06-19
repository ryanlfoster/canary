package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

import java.lang.management.ManagementFactory
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
@MonitoredServiceDefinition(description = 'Examines memory usage and reports back if thresholds are violated', pollInterval = 30, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 10)
@AutomaticResetMonitor(resetInterval = 30, resetIntervalUnit = TimeUnit.SECONDS)
@Slf4j
class JVMMemoryAnalysisMonitor implements MonitoredService {

    @Override
    PollResponse poll() {

       PollResponse.SUCCESS()
    }
}
