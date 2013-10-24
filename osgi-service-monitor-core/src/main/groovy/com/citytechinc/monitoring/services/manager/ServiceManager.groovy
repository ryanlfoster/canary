package com.citytechinc.monitoring.services.manager

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface ServiceManager {

    void forcePoll()

    List<String> listMonitors()

    void informOfShutdown(String service)

}
