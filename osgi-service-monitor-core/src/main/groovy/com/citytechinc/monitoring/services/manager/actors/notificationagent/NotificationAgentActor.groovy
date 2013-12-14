package com.citytechinc.monitoring.services.manager.actors.notificationagent

import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper
import com.citytechinc.monitoring.api.notification.SubscriptionStrategy
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Slf4j
final class NotificationAgentActor extends DynamicDispatchActor {

    static class FlushQueue {}

    NotificationAgentWrapper wrapper
    TimedQueueFlushActor timedQueueFlushActor

    List<ServiceMonitorRecordHolder> queuedMessages = []

    void onMessage(ServiceMonitorRecordHolder message) {

        switch (wrapper.definition.subscriptionStrategy()) {

            case SubscriptionStrategy.opt_into:

                if (wrapper.definition.subscriptionStrategySpecifics().collect { it.name }.contains(message.monitoredService))
                    handleMessage(message)

                break

            case SubscriptionStrategy.opt_out_of:

                if (!wrapper.definition.subscriptionStrategySpecifics().collect { it.name }.contains(message.monitoredService))
                    handleMessage(message)

                break

            case SubscriptionStrategy.all:
            default:

                handleMessage(message)
        }
    }

    void onMessage(FlushQueue message) {

        log.debug("Flushing queue of size ${queuedMessages.size()}")
        wrapper.agent.notify(queuedMessages)
        queuedMessages.clear()
    }

    private handleMessage(ServiceMonitorRecordHolder message) {

        if (wrapper.aggregationWindowInMilliseconds > 0) {

            if (queuedMessages.isEmpty()) {

                log.debug("Starting TimedQueueFlushActor with a sleep time of ${wrapper.aggregationWindowInMilliseconds}")
                timedQueueFlushActor = new TimedQueueFlushActor(sleepTime: wrapper.aggregationWindowInMilliseconds, notificationAgentActor: this)
                timedQueueFlushActor.start()
            }

            log.debug("Adding message. Size of queue is ${queuedMessages.size()}")
            queuedMessages.add(message)

        } else {

            log.debug("Sending message, queue not needed...")
            wrapper.agent.notify([message])
        }
    }

}
