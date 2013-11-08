package com.citytechinc.monitoring.services.responsehandler

import com.citytechinc.monitoring.services.notification.SubscriptionStrategy

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
public @interface PollResponseHandlerDefinition {

    /**
     *
     * Notification strategy for this service.
     *
     * @return
     */
    SubscriptionStrategy subscriptionStrategy()

    /**
     *
     * @return
     */
    Class[] subscriptionStrategySpecifics() default []
}