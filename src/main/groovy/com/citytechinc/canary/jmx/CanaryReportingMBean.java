package com.citytechinc.canary.jmx;

import com.adobe.granite.jmx.annotation.Description;

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

    @Description("Lists monitors and their overall poll results")
    public TabularDataSupport getMonitoredServicesPollResults();

    @Description("Lists notification agents and their statistics")
    public TabularDataSupport getNotificationAgentsStatistics();

    @Description("Lists poll response handlers and their statistics")
    public TabularDataSupport getPollResponseHandlersStatistics();

    @Description("Lists poll response handlers and their statistics")
    public TabularDataSupport getRecordPersistenceServicesStatistics();

}
