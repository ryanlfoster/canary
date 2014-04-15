package com.citytechinc.canary.services.monitor.statemonitoring

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

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
@MonitoredServiceDefinition(description = 'Checks for inactive bundles', pollInterval = 30, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 10)
@AutomaticResetMonitor(resetInterval = 30, resetIntervalUnit = TimeUnit.MINUTES)
class InactiveBundlesMonitor implements MonitoredService {

    @Override
    PollResponse poll() {

        PollResponse.SUCCESS()
    }
}
