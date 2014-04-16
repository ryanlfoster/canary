package com.citytechinc.canary.services.monitor.statemonitoring

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import groovy.util.logging.Slf4j
import org.apache.felix.scr.Component
import org.apache.felix.scr.ScrService
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component as ComponentAnnotation
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.PropertyUnbounded
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Constants as OsgiConstants
import org.osgi.service.cm.ConfigurationAdmin

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@ComponentAnnotation(policy = ConfigurationPolicy.REQUIRE, immediate = true)
@Service
@Properties(value = [
        @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@MonitoredServiceDefinition(description = 'Checks for inactive components', pollInterval = 30, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 12)
@AutomaticResetMonitor(resetInterval = 30, resetIntervalUnit = TimeUnit.SECONDS)
@Slf4j
class InactiveComponentsMonitor implements MonitoredService {

    @Property(name = 'ignoredComponents', label = 'Ignored components', description = 'List of component ids to ignore', unbounded = PropertyUnbounded.ARRAY)
    private List<String> ignoredComponents

    @Reference
    ScrService scrService

    @Reference
    ConfigurationAdmin configurationAdmin

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {

        ignoredComponents = PropertiesUtil.toStringArray(properties.get('ignoredComponents')) as List
    }

    @Override
    PollResponse poll() {

        log.debug("Polling...")

        def messages = []
        def nonExemptComponents = scrService.components.findAll {
            !ignoredComponents.contains(it.id)
        }

        nonExemptComponents.findAll {
            it.state == Component.STATE_DISABLED
        }.each {
            messages.add("The service ${it.id} is disabled.")
        }

        nonExemptComponents.findAll {
            it.state == Component.STATE_UNSATISFIED
        }.findAll {
            configurationAdmin.getConfiguration(it.configurationPid)
        }.each {
            messages.add("The service ${it.id} requires a configuration ${it.configurationPid} and none exists.")
        }

        messages ? PollResponse.ERROR().addMessages(messages.collect { it.toString() }) : PollResponse.SUCCESS()
    }
}
