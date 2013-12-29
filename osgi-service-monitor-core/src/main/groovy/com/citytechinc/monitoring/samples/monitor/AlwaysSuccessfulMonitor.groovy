package com.citytechinc.monitoring.samples.monitor

import com.citytechinc.monitoring.api.monitor.AutoResumingPoller
import com.citytechinc.monitoring.constants.Constants

import com.citytechinc.monitoring.api.monitor.MonitoredService
import com.citytechinc.monitoring.api.monitor.MonitoredServiceDefinition
import com.citytechinc.monitoring.api.monitor.PollResponse
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
@MonitoredServiceDefinition(name = 'Always Successful', description = 'Should always return success', pollFrequency = 1, pollFrequencyUnit = TimeUnit.SECONDS, sequentialFailedPollsToTriggerAlarm = 5)
@AutoResumingPoller(autoResumePollingPeriod = 10, autoResumePollingUnit = TimeUnit.SECONDS)
class AlwaysSuccessfulMonitor implements MonitoredService {

    def random = new Random()

    @Override
    PollResponse poll() {

        def response

        switch(random.nextInt(3)) {

            case 0:
                response = PollResponse.SUCCESS()
                break
            case 1:
                response = PollResponse.SERVICE_UNAVAILABLE()
                break
            case 2:
                response = PollResponse.UNEXPECTED_SERVICE_RESPONSE()
                break
            case 3:
                response = PollResponse.EXCEPTION(new Exception('oops'))
                break
        }

        //response
        PollResponse.SUCCESS()
    }
}
