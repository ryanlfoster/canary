package com.citytechinc.monitoring.services.manager.actors

import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.notification.SubscriptionStrategy
import com.citytechinc.monitoring.services.jcrpersistence.ServiceMonitorRecordHolder
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor
import org.apache.sling.commons.scheduler.Scheduler

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Slf4j
final class NotificationAgentActor extends DynamicDispatchActor {

    static def jobprefix = 'scheduled-notification-agent-flush-'

    // MESSAGES
    static class FlushQueue { }

    NotificationAgentWrapper wrapper
    Scheduler scheduler

    /**
     * Internal message queue is used for batching messages. When a message is processed and an aggregation period
     *   is defined, we place said messages in this list and start a timer. The timer is equal to the aggregation period
     *   which, when expired, instructs this actor to flush its queue (this list).
     */
    List<ServiceMonitorRecordHolder> queuedMessages = []

    /**
     *
     * Performs filtering of ServiceMonitorRecordHolder messages. If the wrapper definition
     *   allows the message to pass, we will call the handler. If the wrapper definition does
     *   not allow the message to pass, the message is dropped.
     *
     * @param message
     */
    void onMessage(ServiceMonitorRecordHolder message) {

        switch (wrapper.definition.strategy()) {

            case SubscriptionStrategy.opt_into:

                if (wrapper.definition.specifics().collect { it.name }.contains(message.monitoredService))
                    handleMessage(message)

                break

            case SubscriptionStrategy.opt_out_of:

                if (!wrapper.definition.specifics().collect { it.name }.contains(message.monitoredService))
                    handleMessage(message)

                break

            case SubscriptionStrategy.all:
            default:

                handleMessage(message)
        }
    }

    void onMessage(FlushQueue message) {

        log.info("Flushing queue of size ${queuedMessages.size()}")

        wrapper.agent.notify(queuedMessages)
        queuedMessages.clear()
    }

    private handleMessage(ServiceMonitorRecordHolder message) {

        if (wrapper.aggregationWindowInMilliseconds > 0) {

            if (queuedMessages.isEmpty()) {

                def now = new Date()
                scheduler.fireJobAt(jobprefix + wrapper.agent.class.name, {

                    this << new FlushQueue()

                }, [:], new Date(now.time + wrapper.aggregationWindowInMilliseconds))
            }

            log.info("Adding message to queue with size of ${queuedMessages.size()}")
            queuedMessages.add(message)

        } else {

            log.info("Aggregation undefined, sending message without delay...")
            wrapper.agent.notify([message])
        }
    }

}
