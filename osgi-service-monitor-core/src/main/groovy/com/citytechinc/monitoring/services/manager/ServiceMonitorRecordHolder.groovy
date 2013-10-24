package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.services.monitor.PollResponseType
import com.citytechinc.monitoring.services.persistence.ServiceMonitorRecord
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
@ToString(includeNames=true)
class ServiceMonitorRecordHolder {

    private Queue<ServiceMonitorRecord> records
    private String monitoredService

    public ServiceMonitorRecordHolder() {
        this(10)
    }

    public ServiceMonitorRecordHolder(Integer numberOfRecords) {
        records = Queues.newArrayBlockingQueue(numberOfRecords)
    }

    void addRecord(ServiceMonitorRecord record) {
        records.offer(record)
    }

    List<ServiceMonitorRecord> getRecords() {
        records as List
    }

    Optional<ServiceMonitorRecord> getFirstSuccessfulPoll() {

        def firstRecord = getRecords().findAll() { it.responseType == PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<ServiceMonitorRecord> getMostRecentSuccessfulPoll() {

        def firstRecord = getRecords().findAll { it.responseType == PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<ServiceMonitorRecord> getFirstFailedPoll() {

        def firstRecord = getRecords().findAll { it.responseType != PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<ServiceMonitorRecord> getMostRecentFailedPoll() {

        def firstRecord = getRecords().findAll { it.responseType != PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Integer getTotalNumberOfFailures() {

        getRecords().count { it.responseType != PollResponseType.success }
    }

    Integer getTotalNumberOfSuccessiveFailures() {
    }

    Optional<List<ServiceMonitorRecord>> getFailures() {

    }
}
