package com.citytechinc.monitoring.api.notification

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

    final NotificationAgent agent
    final NotificationAgentDefinition definition
    final AggregateAlarms aggregateAlarms
    final Long aggregationWindowInMilliseconds

    public NotificationAgentWrapper(NotificationAgent agent) {
        this.agent = agent
        definition = agent.getClass().getAnnotation(NotificationAgentDefinition.class)
        aggregateAlarms = agent.getClass().getAnnotation(AggregateAlarms.class)

        if (aggregateAlarms != null) {
            aggregationWindowInMilliseconds = TimeUnit.MILLISECONDS.convert(aggregateAlarms.aggregationWindow(), aggregateAlarms.aggregationWindowTimeUnit())
        } else {
            aggregationWindowInMilliseconds = 0L
        }
    }
}
