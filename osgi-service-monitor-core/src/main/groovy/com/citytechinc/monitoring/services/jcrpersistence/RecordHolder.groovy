package com.citytechinc.monitoring.services.jcrpersistence

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.monitor.PollResponseType
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
class RecordHolder {

    final Queue<DetailedPollResponse> records
    final String canonicalMonitorName
    final Integer sequentialFailedPollsToTriggerAlarm

    private RecordHolder(String canonicalMonitorName, Integer numberOfRecords, Integer sequentialFailedPollsToTriggerAlarm) {
        this.canonicalMonitorName = canonicalMonitorName
        records = Queues.newArrayBlockingQueue(numberOfRecords)
        this.sequentialFailedPollsToTriggerAlarm = sequentialFailedPollsToTriggerAlarm
    }

    public static CREATE_NEW(MonitoredServiceWrapper wrapper) {

        return new RecordHolder(wrapper.canonicalMonitorName, wrapper.definition.pollHistoryLength(), wrapper.definition.sequentialFailedPollsToTriggerAlarm())
    }

    public static CREATE_FROM_RECORDS(MonitoredServiceWrapper wrapper, List<DetailedPollResponse> records) {

        RecordHolder holder = new RecordHolder(wrapper.canonicalMonitorName, wrapper.definition.pollHistoryLength(), wrapper.definition.sequentialFailedPollsToTriggerAlarm())
        records.each { holder.addRecord(it) }

        holder
    }

    void addRecord(DetailedPollResponse record) {
        records.offer(record)
    }

    void clearAlarm() {
        getRecords().last().cleared = true
    }

    List<DetailedPollResponse> getRecords() {
        records as List
    }

    Boolean isAlarmed() {

        final Boolean alarmed

        if (getRecords().size() < sequentialFailedPollsToTriggerAlarm) {

            alarmed = false
        } else {

            final List<DetailedPollResponse> scrutizedRecords

            if (getRecords().size() > sequentialFailedPollsToTriggerAlarm) {
                scrutizedRecords = Lists.partition(getRecords().reverse(), sequentialFailedPollsToTriggerAlarm).first()
            } else {
                scrutizedRecords = getRecords().reverse()
            }

            alarmed = scrutizedRecords.findAll { it.responseType != PollResponseType.success }.findAll { !it.cleared }.size() > 0
        }

        alarmed
    }

}
