package com.citytechinc.monitoring.services.notification

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public @interface NotificationAgentDefinition {

    /**
     *
     * @return
     */
    boolean aggregateAlarms() default true

    /**
     *
     * @return
     */
    int aggregationPeriodInSections() default 60

    /**
     *
     * @return
     */
    boolean summarizeNotifications() default true
}