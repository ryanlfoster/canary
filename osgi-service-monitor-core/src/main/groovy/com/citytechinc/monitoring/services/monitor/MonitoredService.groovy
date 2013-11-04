package com.citytechinc.monitoring.services.monitor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface MonitoredService {

    /**
     *
     * Instructs a specific Monitor to execute its poll method.
     *
     * @return The poll response and any supporting data (exceptions).
     */
    public PollResponse poll()
}