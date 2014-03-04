package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.day.cq.replication.AgentManager
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
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
@MonitoredServiceDefinition(description = 'Examines replication agents for blocked queues', pollInterval = 3, pollIntervalUnit = TimeUnit.MINUTES, alarmThreshold = 10)
@AutomaticResetMonitor(resetInterval = 3, resetIntervalUnit = TimeUnit.MINUTES)
class BlockedAgentQueueMonitor implements MonitoredService {

    @Property(name = 'agentIds', label = 'Agent IDs', value = ['publish', ''], description = 'Agent IDs to examine for blocked replication queues')
    private List<String> agentIds

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {
        agentIds = PropertiesUtil.toStringArray(properties.get('agentIds')) as List
    }

    @Reference
    AgentManager agentManager

    @Override
    PollResponse poll() {

        StringBuilder message = new StringBuilder()

        agentManager.agents.values().findAll { it.enabled }
                .findAll { agentIds.contains(it.configuration.agentId) }
                .each {

            if (it.queue.status.nextRetryTime > 0) {

                message.append("The replication queue for ${it.configuration.agentId} is blocked. ")
            }
        }

        message.size() > 0 ? PollResponse.SUCCESS() : PollResponse.UNEXPECTED_SERVICE_RESPONSE().addMessage(message)
    }
}
