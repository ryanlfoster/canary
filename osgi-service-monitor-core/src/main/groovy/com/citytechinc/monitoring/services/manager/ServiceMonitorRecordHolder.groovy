package com.citytechinc.monitoring.services.manager

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.monitor.PollResponseType
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import com.google.common.collect.Lists
import com.google.common.collect.Queues
import groovy.transform.ToString
import groovy.util.logging.Slf4j

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@ToString(includeFields=true)
@Slf4j
class ServiceMonitorRecordHolder {

    private final Queue<DetailedPollResponse> records
    private final String monitoredService
    private final Integer sequentialFailedPollsToTriggerAlarm

    private ServiceMonitorRecordHolder(String monitoredService, Integer numberOfRecords, Integer sequentialFailedPollsToTriggerAlarm) {
        this.monitoredService = monitoredService
        records = Queues.newArrayBlockingQueue(numberOfRecords)
        this.sequentialFailedPollsToTriggerAlarm = sequentialFailedPollsToTriggerAlarm
    }

    public static CREATE_NEW(MonitoredServiceWrapper wrapper) {

        return new ServiceMonitorRecordHolder(wrapper.monitorServiceClassName, wrapper.definition.pollHistoryLength(), wrapper.definition.sequentialFailedPollsToTriggerAlarm())
    }

    public static CREATE_FROM_RECORDS(MonitoredServiceWrapper wrapper, List<DetailedPollResponse> records) {

        ServiceMonitorRecordHolder holder = new ServiceMonitorRecordHolder(wrapper.monitorServiceClassName, wrapper.definition.pollHistoryLength(), wrapper.definition.sequentialFailedPollsToTriggerAlarm())
        records.each { holder.addRecord(it) }

        holder
    }

    void addRecord(DetailedPollResponse record) {
        records.offer(record)
    }

    List<DetailedPollResponse> getRecords() {
        records as List
    }

    Boolean isAlarmed() {

        def alarmed = false

        def topPollResults = Lists.partition(getRecords().reverse(), sequentialFailedPollsToTriggerAlarm).first()

        if (topPollResults.size() == sequentialFailedPollsToTriggerAlarm) {

            alarmed = topPollResults.findAll { it.responseType != PollResponseType.success }.findAll { it.responseType != PollResponseType.clear }.size() > 0
        }

        alarmed
    }
}
