package com.citytechinc.canary.jmx;

import com.adobe.granite.jmx.annotation.Description;

import javax.management.openmbean.TabularDataSupport;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Description("CITYTECH Canary Framework Configured State Monitoring")
public interface CanaryConfiguredStateMonitoringMBean {

    @Description("Lists monitored components")
    public TabularDataSupport getMonitoredComponentStateConfigurations();

    @Description("Lists monitored bundles")
    public TabularDataSupport getMonitoredBundleStateConfigurations();

}
