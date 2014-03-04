package com.citytechinc.canary.api.notification

import groovy.transform.EqualsAndHashCode
import org.codehaus.jackson.annotate.JsonIgnore

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@EqualsAndHashCode
public final class NotificationAgentWrapper {

    @JsonIgnore
    @Delegate final NotificationAgent agent
    final String identifier
    final NotificationAgentDefinition definition
    final AggregateAlarms aggregateAlarms

    public NotificationAgentWrapper(NotificationAgent agent) {
        this.agent = agent
        identifier = agent.class.canonicalName
        definition = agent.getClass().getAnnotation(NotificationAgentDefinition)
        aggregateAlarms = agent.getClass().getAnnotation(AggregateAlarms)
    }
}
