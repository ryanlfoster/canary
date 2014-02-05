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
@Description("CITYTECH OSGi Service Monitor Management and Reporting")
public interface ServiceMonitorManagerMBean {

    @Description("Lists poll response handlers and statistics")
    public TabularDataSupport getMonitors();

    @Description("Lists notification agents and statistics")
    public TabularDataSupport getNotificationAgents();

    @Description("Lists poll response handlers and statistics")
    public TabularDataSupport getPollResponseHandlers();

    @Description("Lists registered poll response handlers and statistics")
    public TabularDataSupport getRecordPersistenceServices();

    @Description("Get records for a specific monitor")
    public TabularDataSupport getRecordsForMonitor(@Name("monitoredService") @Description("The fully qualified path of a monitored service") String monitoredService);

    @Description("Send poll request to all monitors")
    public void requestAllMonitorsPoll();

    @Description("Send persistence request to all monitors")
    public void requestAllMonitorsPersist();

    @Description("Send alarm reset message to all monitors")
    public void resetAllAlarms();

    @Description("Send alarm reset message to a specific monitor")
    public void resetAlarm(@Name("monitoredService") @Description("The fully qualified path of a monitored service") String monitoredService);

}
