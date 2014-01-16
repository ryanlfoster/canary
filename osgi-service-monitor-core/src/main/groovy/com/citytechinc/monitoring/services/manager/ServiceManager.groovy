package com.citytechinc.monitoring.services.manager

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface ServiceManager {

    /**
     *
     */
    public void requestAllMonitorsPoll();

    /**
     *
     */
    public void requestAllMonitorsPersist();

    /**
     *
     * @return
     */
    public List<String> listMonitoredServices()

    /**
     *
     * @return
     */
    public List<String> listRecordPersistenceServices()

    /**
     *
     * @return
     */
    public List<String> listNotificationAgents()

    /**
     *
     * @return
     */
    public List<String> listPollResponseHandlers()

    /**
     *
     * @return
     */
    public List<String> listAlarmedMonitors()

    /**
     *
     * @param identifer
     * @return
     */
    ServiceMonitorRecordHolder getRecordHolder(String identifer)

}
