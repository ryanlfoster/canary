package com.citytechinc.canary.api.notification

import groovy.transform.EqualsAndHashCode

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@EqualsAndHashCode
public final class NotificationAgentWrapper {

    @Delegate final NotificationAgent agent
    final String identifier
    final NotificationAgentDefinition definition
    final AggregateAlarms aggregateAlarms
    final Long aggregationWindowInMilliseconds

    public NotificationAgentWrapper(NotificationAgent agent) {
        this.agent = agent
        identifier = agent.class.canonicalName
        definition = agent.getClass().getAnnotation(NotificationAgentDefinition.class)
        aggregateAlarms = agent.getClass().getAnnotation(AggregateAlarms.class)

        if (aggregateAlarms != null) {
            aggregationWindowInMilliseconds = TimeUnit.MILLISECONDS.convert(aggregateAlarms.aggregationWindow(), aggregateAlarms.aggregationWindowTimeUnit())
        } else {
            aggregationWindowInMilliseconds = 0L
        }
    }
}
