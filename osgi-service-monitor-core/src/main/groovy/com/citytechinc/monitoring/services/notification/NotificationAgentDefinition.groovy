package com.citytechinc.monitoring.services.notification

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
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

    /**
     *
     * Notification strategy for this service.
     *
     * @return
     */
    SubscriptionStrategy subscriptionStrategy() default SubscriptionStrategy.all

    /**
     *
     * @return
     */
    String[] subscriptionStrategySpecifics() default []
}