package com.citytechinc.canary.jmx;

import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;

import javax.management.openmbean.TabularDataSupport;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Description("CITYTECH Canary Framework Reporting")
public interface CanaryReportingMBean {

    @Description("Lists monitors and their configurations")
    public TabularDataSupport getMonitoredServicesConfigurations();

    @Description("Lists notification agents and their configurations")
    public TabularDataSupport getNotificationAgentsConfigurations();

    @Description("Lists poll response handlers and their configurations")
    public TabularDataSupport getPollResponseHandlersConfigurations();

    @Description("Lists record persistence services and their configurations")
    public TabularDataSupport getRecordPersistenceServicesConfigurations();

    @Description("Lists monitors and their results")
    public TabularDataSupport getMonitoredServicesResults();

    @Description("Lists notification agents and their statistics")
    public TabularDataSupport getNotificationAgentsResults();

    @Description("Lists poll response handlers and their statistics")
    public TabularDataSupport getPollResponseHandlersResults();

    @Description("Lists poll response handlers and their statistics")
    public TabularDataSupport getRecordPersistenceServicesResults();

}
