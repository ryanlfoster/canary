package com.citytechinc.canary.services.logescalation

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
public interface LogEscalationManager {

    /**
     *
     * @param servicename
     */
    String escalate(String servicename)

    /**
     *
     * @param servicename
     */
    void deescalate(String servicename)
}