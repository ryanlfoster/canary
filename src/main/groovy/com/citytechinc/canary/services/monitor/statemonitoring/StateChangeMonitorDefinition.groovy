package com.citytechinc.canary.services.monitor.statemonitoring

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
public interface StateChangeMonitorDefinition {

    /**
     *
     * @return Bundle name to monitor
     */
    String getBundleName()

    /**
     *
     * @return
     */
    String getPolicy()

    /**
     *
     * @return
     */
    List<String> getSpecifics()

}