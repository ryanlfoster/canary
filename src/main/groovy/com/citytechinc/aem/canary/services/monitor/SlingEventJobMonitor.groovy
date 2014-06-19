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
@MonitoredServiceDefinition(description = 'Polls the sling job manager examining average wait and process times', pollInterval = 30, pollIntervalUnit = TimeUnit.SECONDS)
@AutomaticResetMonitor(resetInterval = 30, resetIntervalUnit = TimeUnit.SECONDS)
@Slf4j
class SlingEventJobMonitor implements MonitoredService {

    @Reference
    JobManager jobManager

    @Property(name = 'averageProcessingTimeThreshold', label = 'Average Process Time Failure Threshold', longValue = 100L, description = 'The average process time threshold limit in ms. If the actual measurement exceeds this value, the response indicates a failed or unhealthy poll.')
    private Long averageProcessingTimeThreshold

    @Property(name = 'averageWaitingTimeThreshold', label = 'Average Waiting Time Failure Threshold', longValue = 100L, description = 'The average waiting time threshold limit in ms. If the actual measurement exceeds this value, the response indicates a failed or unhealthy poll.')
    private Long averageWaitingTimeThreshold

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws Exception {

        averageProcessingTimeThreshold = PropertiesUtil.toLong(properties.get('averageProcessingTimeThreshold'), 100L)
        averageWaitingTimeThreshold = PropertiesUtil.toLong(properties.get('averageWaitingTimeThreshold'), 100L)
    }

    @Override
    PollResponse poll() {

        def response = PollResponse.SUCCESS()

        log.debug("Polling. Actual process time average is: ${jobManager.statistics.averageProcessingTime}," +
                " configured threshold is: ${averageProcessingTimeThreshold}. Actual wait time average is:" +
                " ${jobManager.statistics.averageWaitingTime}, configured threshold is ${averageWaitingTimeThreshold}")

        if (jobManager.statistics.averageProcessingTime > averageProcessingTimeThreshold ||
                jobManager.statistics.averageWaitingTime > averageWaitingTimeThreshold) {

            response = PollResponse.WARNING()

            if (jobManager.statistics.averageProcessingTime > averageProcessingTimeThreshold) {
                response.addMessage("Average processing time of ${jobManager.statistics.averageProcessingTime} exceeds threshold of ${averageProcessingTimeThreshold}")
            }

            if (jobManager.statistics.averageWaitingTime > averageWaitingTimeThreshold) {
                response.addMessage("Average waiting time of ${jobManager.statistics.averageWaitingTime} exceeds threshold of ${averageWaitingTimeThreshold}")
            }

        }

        response
    }
}
