package com.citytechinc.canary.services.monitor.statemonitoring

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
import org.apache.felix.scr.annotations.PropertyUnbounded
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import org.osgi.framework.Constants as OsgiConstants
import org.osgi.service.component.ComponentContext

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
@Slf4j
class InactiveBundlesMonitor implements MonitoredService {

    @Property(name = 'ignoredBundles', label = 'Ignored Bundles', description = 'List of bundle symbolic names to ignore', unbounded = PropertyUnbounded.ARRAY)
    private List<String> ignoredBundles

    BundleContext bundleContext

    @Activate
    @Modified
    protected void activate(ComponentContext componentContext) throws Exception {

        bundleContext = componentContext.bundleContext
        ignoredBundles = PropertiesUtil.toStringArray(componentContext.properties.get('ignoredBundles')) as List
    }

    @Override
    PollResponse poll() {

        log.debug("Polling...")

        def messages = bundleContext.bundles.findAll {

            (!ignoredBundles.contains(it.symbolicName) &&

                    (it.state != Bundle.ACTIVE

                            || !(it.state == Bundle.STARTING && it.headers.get(OsgiConstants.BUNDLE_ACTIVATIONPOLICY).equals(OsgiConstants.ACTIVATION_LAZY))
                            || !it.headers.get(OsgiConstants.FRAGMENT_HOST)))
        }.collect {

            "The bundle ${it.symbolicName} is not in a healthy state".toString()
        }

        messages ? PollResponse.ERROR().addMessages(messages) : PollResponse.SUCCESS()
    }
}
