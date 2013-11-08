package com.citytechinc.monitoring.services.notification;

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

    public NotificationAgentWrapper(NotificationAgent agent) {
        this.agent = agent;
        this.definition = agent.getClass().getAnnotation(NotificationAgentDefinition.class);
    }

    public NotificationAgent getAgent() {
        return agent;
    }

    public NotificationAgentDefinition getDefinition() {
        return definition;
    }
}
