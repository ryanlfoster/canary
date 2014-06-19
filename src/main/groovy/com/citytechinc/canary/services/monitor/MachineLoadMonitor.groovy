package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
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
@MonitoredServiceDefinition(description = 'Checks for machine load over a threshold', pollInterval = 30, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 10)
@AutomaticResetMonitor(resetInterval = 30, resetIntervalUnit = TimeUnit.SECONDS)
@Slf4j
class MachineLoadMonitor implements MonitoredService {

    @Property(name = 'warningThreshold', label = 'Response generation time warning threshold', longValue = 10L, description = '')
    private Long warningThreshold

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {
        warningThreshold = PropertiesUtil.toLong(properties.get('warningThreshold'), 10L)
    }

    @Override
    PollResponse poll() {

        ManagementFactory.operatingSystemMXBean.systemLoadAverage

        PollResponse.ERROR()
    }
}
