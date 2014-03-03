package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.apache.sling.event.jobs.JobManager
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
@MonitoredServiceDefinition(description = 'Polls the sling job manager examining average wait and process times', pollInterval = 5, pollIntervalUnit = TimeUnit.SECONDS)
class SlingEventJobMonitor implements MonitoredService {

    @Reference
    JobManager jobManager

    @Property(name = 'averageProcessingTimeThreshold', label = 'Average Process Time Failure Threshold', longValue = 100L, description = 'The average process time threshold limit in ms. If the actual measurement exceeds this value, the response indicates a failed or unhealthy poll.')
    private Long averageProcessingTimeThreshold

    @Property(name = 'averageWaitingTimeThreshold', label = 'Average Waiting Time Failure Threshold', longValue = 100L, description = 'The average waiting time threshold limit in ms. If the actual measurement exceeds this value, the response indicates a failed or unhealthy poll.')
    private String averageWaitingTimeThreshold

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws Exception {

        averageProcessingTimeThreshold = PropertiesUtil.toLong(properties.get('averageProcessingTimeThreshold'), 100L)
        averageWaitingTimeThreshold = PropertiesUtil.toLong(properties.get('averageWaitingTimeThreshold'), 100L)
    }

    @Override
    PollResponse poll() {

        if (jobManager.statistics.averageProcessingTime > averageProcessingTimeThreshold ||
                jobManager.statistics.averageWaitingTime > averageWaitingTimeThreshold) {

            PollResponse.UNEXPECTED_SERVICE_RESPONSE()
        } else {
            PollResponse.SUCCESS()
        }
    }
}
