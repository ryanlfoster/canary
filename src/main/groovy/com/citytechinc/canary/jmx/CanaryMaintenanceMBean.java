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
@Description("CITYTECH Canary Framework Maintenance")
public interface CanaryMaintenanceMBean {

    @Description("Lists monitors")
    public TabularDataSupport getMonitors();

    @Description("Get records for a specific monitor")
    public TabularDataSupport getRecordsForMonitor(@Name("monitoredService") @Description("The fully qualified path of a monitored service") String monitoredService);

    @Description("Returns a stacktrace for a monitor")
    public String getStacktraceForMonitor(@Name("monitoredService") @Description("The fully qualified path of a monitored service") String monitoredService, @Name("startDate") @Description("The start date supplied in the format yyyy-MM-dd HH:mm:ss") String startDate);

    @Description("Send poll request to ALL monitors")
    public void requestAllMonitorsPoll();

    @Description("Send persistence request to ALL monitors")
    public void requestAllMonitorsPersist();

    @Description("Send alarm reset message to ALL monitors")
    public void resetAllAlarms();

    @Description("Send alarm reset message to a specific monitor")
    public void resetAlarm(@Name("monitoredService") @Description("The fully qualified path of a monitored service") String monitoredService);

}
