package com.citytechinc.monitoring.samples.monitor

import com.citytechinc.monitoring.constants.Constants

import com.citytechinc.monitoring.services.monitor.MonitoredService
import com.citytechinc.monitoring.services.monitor.MonitoredServiceDefinition
import com.citytechinc.monitoring.services.monitor.PollResponse
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@MonitoredServiceDefinition(name = 'Always Successful', description = 'Should always return success', pollFrequency = 10, pollFrequencyUnit = TimeUnit.SECONDS)
class AlwaysSuccessfulMonitor implements MonitoredService {

    @Override
    PollResponse poll() {
        PollResponse.SUCCESS()
    }
}
