package com.citytechinc.canary.api.notification

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
     * Notification strategy for this service.
     *
     * @return
     */
    SubscriptionStrategy strategy()

    /**
     *
     * @return
     */
    String[] specifics() default []

    /**
     *
     * The maximum execution time allowed for an operation. This is used to protect the inner workings of
     *   the framework.
     *
     * @return
     */
    long maxExecutionTimeInMilliseconds() default 500L
}