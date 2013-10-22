package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.services.monitor.PollResponseType
import com.citytechinc.monitoring.services.persistence.ServiceMonitorRecord
import com.google.common.base.Optional
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

    Queue<ServiceMonitorRecord> records

    List<ServiceMonitorRecord> getRecordsAsList() {
        records as List
    }

    Optional<ServiceMonitorRecord> getFirstSuccessfulPoll() {

        def firstRecord = getRecordsAsList().collect { it.responseType == PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<ServiceMonitorRecord> getMostRecentSuccessfulPoll() {

        def firstRecord = getRecordsAsList().collect { it.responseType == PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<ServiceMonitorRecord> getFirstFailedPoll() {

        def firstRecord = getRecordsAsList().collect { it.responseType != PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Optional<ServiceMonitorRecord> getMostRecentFailedPoll() {

        def firstRecord = getRecordsAsList().collect { it.responseType != PollResponseType.success }.sort { it.startTime }.first()
        firstRecord ? Optional.of(firstRecord) : Optional.absent()
    }

    Integer getTotalNumberOfFailures() {

        getRecordsAsList().count { it.responseType != PollResponseType.success }
    }

    Integer getTotalNumberOfSuccessiveFailures() {
    }

    Optional<List<ServiceMonitorRecord>> getFailures() {

    }
}
