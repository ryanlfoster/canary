package com.citytechinc.monitoring.api.responsehandler

import com.citytechinc.monitoring.api.notification.SubscriptionStrategy

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PollResponseDefinition {

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
    Class[] specifics() default []
}
