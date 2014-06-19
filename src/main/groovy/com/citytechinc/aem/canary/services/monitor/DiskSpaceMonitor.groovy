package com.citytechinc.aem.canary.services.monitor

import com.citytechinc.aem.canary.Constants
import com.citytechinc.aem.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.aem.canary.api.monitor.MonitoredService
import com.citytechinc.aem.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.aem.canary.api.monitor.PollResponse
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.apache.sling.settings.SlingSettingsService
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
@MonitoredServiceDefinition(description = 'Examines disk space', pollInterval = 30, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 10)
@AutomaticResetMonitor(resetInterval = 30, resetIntervalUnit = TimeUnit.SECONDS)
@Slf4j
class DiskSpaceMonitor implements MonitoredService {

    @Reference
    SlingSettingsService slingSettingsService

    @Property(name = 'minDiskSpaceThreshold', label = 'The minimum disk space allowed before throwing an error (in megabytes)', longValue = 2500L, description = '')
    private Long minDiskSpaceThreshold

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {
        minDiskSpaceThreshold = PropertiesUtil.toLong(properties.get('minDiskSpaceThreshold'), 2500L)
    }

    @Override
    PollResponse poll() {

        def diskSpaceInMegabytes = new File(slingSettingsService.slingHomePath).freeSpace / 1024L

        diskSpaceInMegabytes > minDiskSpaceThreshold ? PollResponse.SUCCESS() : PollResponse.WARNING().addMessage("Disk space is below threshold of ${minDiskSpaceThreshold}MB at ${diskSpaceInMegabytes}MB")
    }
}
