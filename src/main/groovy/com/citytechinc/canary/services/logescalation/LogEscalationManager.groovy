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
     * @return
     */
    void escalateLogForService(String servicename)

    /**
     *
     */
    void deescalateLogForService(String servicename)
}