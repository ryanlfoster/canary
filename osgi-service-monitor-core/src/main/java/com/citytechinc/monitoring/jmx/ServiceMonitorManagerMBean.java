package com.citytechinc.monitoring.jmx;

import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;

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

    @Description("Lists the monitors and their definitions")
    public TabularDataSupport listMonitoredServices();

    @Description("Lists the persistence services and their definitions")
    public TabularDataSupport listRecordPersistenceServices();

    @Description("Lists the notification agents and their definitions")
    public TabularDataSupport listNotificationAgents();

    @Description("Lists the poll response handlers and their definitions")
    public TabularDataSupport listPollResponseHandlers();

    @Description("Lists alarmed monitors")
    public List<String> listAlarmedMonitors();

    @Description("Resets all alarmed monitors")
    public void resetAllAlarms();

    @Description("Reset a specific alarmed ServiceMonitor")
    public void resetAlarm(@Name("monitoredService") @Description("The fully qualified path of a monitored service") String monitoredService);

}
