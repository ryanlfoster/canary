package com.citytechinc.canary.services.monitor.statemonitoring

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import org.apache.felix.scr.Component
import org.apache.felix.scr.ScrService
import org.apache.felix.scr.annotations.Component as ComponentAnnotation
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
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
@MonitoredServiceDefinition(description = 'Checks for inactive components', pollInterval = 10, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 12)
@AutomaticResetMonitor(resetInterval = 10, resetIntervalUnit = TimeUnit.SECONDS)
class InactiveComponentsMonitor implements MonitoredService {

    @Reference
    ScrService scrService

    @Reference
    ConfigurationAdmin configurationAdmin

    @Override
    PollResponse poll() {

        def messages = []

        scrService.components.findAll {
            it.state == Component.STATE_DISABLED
        }.each {
            messages.add("The service ${it.id} is disabled.")
        }

        scrService.components.findAll {
            it.state == Component.STATE_UNSATISFIED
        }.findAll {
            configurationAdmin.getConfiguration(it.configurationPid)
        }.each {
            messages.add("The service ${it.id} requires a configuration ${it.configurationPid} and none exists.")
        }

        messages ? PollResponse.ERROR().addMessages(messages.collect { it.toString() }) : PollResponse.SUCCESS()
    }
}
