package com.citytechinc.aem.canary.api.responsehandler

import com.citytechinc.aem.canary.api.notification.SubscriptionStrategy

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
public @interface PollResponseHandlerDefinition {

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

}
