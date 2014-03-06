package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.citytechinc.canary.api.responsehandler.PollResponseHandler
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
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
@Component(immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@MonitoredServiceDefinition(description = '', pollInterval = 10, pollIntervalUnit = TimeUnit.SECONDS, maxNumberOfRecords = 100, alarmThreshold = 40)
class TestMonitorB implements MonitoredService {

    def random = new Random()

    @Override
    PollResponse poll() {

        random.nextBoolean() ? PollResponse.SUCCESS() : PollResponse.UNEXPECTED_SERVICE_RESPONSE()
    }
}
