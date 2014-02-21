package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.day.cq.replication.AgentManager
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
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
@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@MonitoredServiceDefinition(description = 'Keeps runtime statistics regarding your instances replication queues.', pollInterval = 5, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 10)
class ReplicationQueueWatchdog implements MonitoredService {

    @Reference
    AgentManager agentManager

    @Override
    PollResponse poll() {

        agentManager.agents.get('publish').queue.entries().size() > 0 ? PollResponse.UNEXPECTED_SERVICE_RESPONSE() : PollResponse.SUCCESS()
    }
}
