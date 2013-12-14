package com.citytechinc.monitoring.api.notification;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public final class NotificationAgentWrapper {

    private final NotificationAgent agent;
    private final NotificationAgentDefinition definition;
    private final AggregateAlarms aggregateAlarms;
    private final Long aggregationWindowInMilliseconds;

    public NotificationAgentWrapper(NotificationAgent agent) {
        this.agent = agent;
        definition = agent.getClass().getAnnotation(NotificationAgentDefinition.class);
        aggregateAlarms = agent.getClass().getAnnotation(AggregateAlarms.class);

        if (aggregateAlarms != null) {
            aggregationWindowInMilliseconds = TimeUnit.MILLISECONDS.convert(aggregateAlarms.aggregationWindow(), aggregateAlarms.aggregationWindowTimeUnit());
        } else {
            aggregationWindowInMilliseconds = 0L;
        }
    }

    public NotificationAgent getAgent() {
        return agent;
    }

    public NotificationAgentDefinition getDefinition() {
        return definition;
    }

    public Long getAggregationWindowInMilliseconds() {
        return aggregationWindowInMilliseconds;
    }
}
