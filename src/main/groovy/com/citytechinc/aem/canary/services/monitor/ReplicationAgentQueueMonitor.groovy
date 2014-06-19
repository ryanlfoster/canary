package com.citytechinc.aem.canary.services.monitor

import com.citytechinc.aem.canary.Constants
import com.citytechinc.aem.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.aem.canary.api.monitor.MonitoredService
import com.citytechinc.aem.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.aem.canary.api.monitor.PollResponse
import com.day.cq.replication.AgentManager
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.PropertyUnbounded
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
@MonitoredServiceDefinition(description = 'Examines replication agents for blocked queues', pollInterval = 10, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 12)
@AutomaticResetMonitor(resetInterval = 3, resetIntervalUnit = TimeUnit.MINUTES)
@Slf4j
class ReplicationAgentQueueMonitor implements MonitoredService {

    @Property(name = 'agentIds', label = 'Agent IDs', description = 'Agent IDs to examine for blocked replication queues', unbounded = PropertyUnbounded.ARRAY)
    private List<String> agentIds

    @Property(name = 'queueWarningThreshold', label = 'Queue size warning threshold', intValue =  50, description = 'The threshold used to indicate a warning')
    private Integer queueWarningThreshold

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {
        agentIds = PropertiesUtil.toStringArray(properties.get('agentIds')) as List
        queueWarningThreshold = PropertiesUtil.toInteger(properties.get('queueWarningThreshold'), 50)
    }

    @Reference
    AgentManager agentManager

    @Override
    PollResponse poll() {

        log.debug("Polling. Scrutinizing agent ids: ${agentIds} of ${agentManager.agents.size()} available agents...")

        def blockedQueueMessages = []
        def queueOversizeMessages = []

        agentManager.agents.values()
                .findAll { it.enabled }
                .findAll { agentIds.contains(it.configuration.agentId) }
                .each {

            log.trace("Agent id: ${it.id} next retry is: ${it.queue.status.nextRetryTime}, queue size is: ${it.queue.entries().size()}")

            if (it.queue.status.nextRetryTime > 0) {

                blockedQueueMessages << "The replication queue for agent: '${it.configuration.agentId}' is blocked.".toString()
            } else if (it.queue.entries().size() > queueWarningThreshold) {

                queueOversizeMessages << "The replication queue for agent: '${it.configuration.agentId}' is above threshold.".toString()
            } else {

                log.debug("Agent: ${it.configuration.agentId} is functioning properly")
            }
        }

        if (blockedQueueMessages) {

            PollResponse.ERROR().addMessages(blockedQueueMessages).addMessages(queueOversizeMessages)
        } else if (queueOversizeMessages) {

            PollResponse.WARNING().addMessages(queueOversizeMessages)
        } else {

            PollResponse.SUCCESS()
        }
    }
}
