package com.citytechinc.monitoring.jmx;

import com.adobe.granite.jmx.annotation.Description;

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

    @Description("Force all monitors to poll 'now' -- monitors will continue polling on registered intervals")
    public void requestAllMonitorsPoll();

    @Description("Force a persistence action for all monitors to all record persistence services")
    public void requestAllMonitorsPersist();

    @Description("Lists the monitors")
    public List<String> listMonitoredServices();

    @Description("Lists the persistence services")
    public List<String> listRecordPersistenceServices();

    @Description("Lists the notification agents")
    public List<String> listNotificationAgents();

    @Description("Lists the poll response handlers")
    public List<String> listPollResponseHandlers();

    @Description("Lists alarmed monitors")
    public List<String> listAlarmedMonitors();

}
