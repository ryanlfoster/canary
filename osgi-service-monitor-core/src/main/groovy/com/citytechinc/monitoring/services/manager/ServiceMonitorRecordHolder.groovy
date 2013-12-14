package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.monitor.PollResponseType
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import com.google.common.base.Optional
import com.google.common.collect.Queues
import groovy.transform.ToString

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@ToString(includeFields=true)
class ServiceMonitorRecordHolder {

    private Queue<DetailedPollResponse> records
    private String monitoredService

    private ServiceMonitorRecordHolder(String monitoredService, Integer numberOfRecords) {
        this.monitoredService = monitoredService
        records = Queues.newArrayBlockingQueue(numberOfRecords)
    }

    public static CREATE_NEW(MonitoredServiceWrapper wrapper) {

        return new ServiceMonitorRecordHolder(wrapper.monitorServiceClassName, wrapper.definition.pollHistoryLength())
    }

    public static CREATE_FROM_RECORDS(MonitoredServiceWrapper wrapper, List<DetailedPollResponse> records) {

        ServiceMonitorRecordHolder holder = new ServiceMonitorRecordHolder(wrapper.monitorServiceClassName, wrapper.definition.pollHistoryLength())
        records.each { holder.addRecord(it) }

        holder
    }

    void addRecord(DetailedPollResponse record) {
        records.offer(record)
    }

    List<DetailedPollResponse> getRecords() {
        records as List
    }

    Optional<DetailedPollResponse> getFirstSuccessfulPoll() {

        def firstRecord = getRecords().findAll() { it.responseType == PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<DetailedPollResponse> getMostRecentSuccessfulPoll() {

        def firstRecord = getRecords().findAll { it.responseType == PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<DetailedPollResponse> getFirstFailedPoll() {

        def firstRecord = getRecords().findAll { it.responseType != PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<DetailedPollResponse> getMostRecentFailedPoll() {

        def firstRecord = getRecords().findAll { it.responseType != PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    String getMonitoredService() {
        return monitoredService
    }

    Integer getTotalNumberOfFailures() {

        getRecords().count { it.responseType != PollResponseType.success }
    }

    Integer getTotalNumberOfSuccessiveFailures() {
    }

    Optional<List<DetailedPollResponse>> getFailures() {

    }
}
