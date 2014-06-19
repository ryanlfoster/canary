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

    @Delegate
    final NotificationAgent agent

    final String identifier
    final SubscriptionStrategy strategy
    final List<String> specifics

    // AGGREGATION
    final Boolean aggregationCriteriaDefined
    final Integer aggregationWindow
    final TimeUnit aggregationWindowTimeUnit

    public NotificationAgentWrapper(NotificationAgent agent) {

        this.agent = agent

        def definition = agent.getClass().getAnnotation(NotificationAgentDefinition)
        def aggregateAlarms = agent.getClass().getAnnotation(AggregateAlarms)

        identifier = agent.class.canonicalName

        strategy = definition.strategy()
        specifics = definition.specifics()

        aggregationCriteriaDefined = aggregateAlarms != null
        aggregationWindow = aggregationCriteriaDefined ? aggregateAlarms.aggregationWindow() : null
        aggregationWindowTimeUnit = aggregationCriteriaDefined ? aggregateAlarms.aggregationWindowTimeUnit() : null
    }
}
