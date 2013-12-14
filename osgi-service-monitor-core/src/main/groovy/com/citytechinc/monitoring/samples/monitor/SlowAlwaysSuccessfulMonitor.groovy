package com.citytechinc.monitoring.samples.monitor

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.monitor.MonitoredServiceDefinition
import com.citytechinc.monitoring.api.monitor.PollResponse
import com.citytechinc.monitoring.constants.Constants
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
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
@MonitoredServiceDefinition(name = 'Slow Always Successful', description = 'Should always return success', pollFrequency = 1, pollFrequencyUnit = TimeUnit.MINUTES)
class SlowAlwaysSuccessfulMonitor implements MonitoredService {

    @Override
    PollResponse poll() {
        PollResponse.SUCCESS()
    }
}
