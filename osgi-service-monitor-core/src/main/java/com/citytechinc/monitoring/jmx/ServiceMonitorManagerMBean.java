package com.citytechinc.monitoring.jmx;

import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;

import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import java.util.List;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Description("CITYTECH OSGi Service Monitor Management")
public interface ServiceMonitorManagerMBean {

    @Description("Request that all monitors poll now. Monitors will continue polling at their scheduled intervals. Alarmed monitors will not poll.")
    public void requestAllMonitorsPoll();

    @Description("Request that all monitors persist their data")
    public void requestAllMonitorsPersist();

    @Description("Lists registered monitor definitions, such as polling intervals, record container sizes, etc...")
    public TabularDataSupport getMonitorDefinitions();

    @Description("Provides an at-a-glance view of the state of all monitors")
    public TabularDataSupport getMonitorStates();

    @Description("Fill me in...")
    public TabularDataSupport getPollResponseHandlerDefinitions();

    @Description("Lists registered poll response handlers, number of processed requests, average execution time, etc...")
    public TabularDataSupport getPollResponseHandlerStatistics();

    @Description("Get records for a specific monitor")
    public TabularDataSupport getRecordsForMonitor(@Name("monitoredService") @Description("The fully qualified path of a monitored service") String monitoredService);

    @Description("Resets all alarmed monitors")
    public void resetAllAlarms();

    @Description("Reset a specific alarmed ServiceMonitor")
    public void resetAlarm(@Name("monitoredService") @Description("The fully qualified path of a monitored service") String monitoredService);

}
