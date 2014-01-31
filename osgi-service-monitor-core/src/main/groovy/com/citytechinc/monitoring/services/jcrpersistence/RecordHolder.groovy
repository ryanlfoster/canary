package com.citytechinc.monitoring.services.jcrpersistence

import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper
import com.citytechinc.monitoring.api.monitor.PollResponseType
import com.google.common.base.Optional
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
    final Integer alarmThreshold
    final Integer maxNumberOfRecords
    Integer totalNumberOfPolls = 0
    Integer totalFailures = 0

    private RecordHolder(String canonicalMonitorName, Integer maxNumberOfRecords, Integer alarmThreshold) {
        this.canonicalMonitorName = canonicalMonitorName
        this.alarmThreshold = alarmThreshold
        this.maxNumberOfRecords = maxNumberOfRecords

        records = Queues.newArrayBlockingQueue(maxNumberOfRecords)
    }

    public static CREATE_NEW(MonitoredServiceWrapper wrapper) {

        return new RecordHolder(wrapper.canonicalMonitorName, wrapper.definition.maxPollHistoryEntries(), wrapper.definition.alarmThreshold())
    }

    public static CREATE_FROM_RECORDS(MonitoredServiceWrapper wrapper, List<DetailedPollResponse> records) {

        RecordHolder holder = new RecordHolder(wrapper.canonicalMonitorName, wrapper.definition.maxPollHistoryEntries(), wrapper.definition.alarmThreshold())
        records.each { holder.addRecord(it) }

        holder
    }

    void addRecord(DetailedPollResponse record) {

        ++totalNumberOfPolls

        if (record.responseType != PollResponseType.success) {
            ++totalFailures
        }

        if (records.size() == maxNumberOfRecords) {
            records.remove()
        }

        records.offer(record)
    }

    void clearAlarm() {
        getRecords().last().cleared = true
    }

    List<DetailedPollResponse> getRecords() {
        records as List
    }

    Optional<Date> firstPoll() {

        Optional<Date> firstPoll = records.empty ? Optional.absent() : Optional.of(records.peek().startTime)
    }

    Optional<Date> mostRecentPollDate() {

        Optional<Date> mostRecentPollDate = records.empty ? Optional.absent() : Optional.of(getRecords().reverse().first().startTime)
    }

    Optional<PollResponseType> mostRecentPollResponse() {

        Optional<PollResponseType> mostRecentPollResponse = records.empty ? Optional.absent() : Optional.of(getRecords().reverse().first().responseType)
    }

    Integer numberOfPolls() {
        records.size()
    }

    Integer numberOfFailures() {
        getRecords().count { it.responseType != PollResponseType.success}
    }

    Long averagePollExecutionTime() {

        Long averageExecutionTime = 0L

        if (records.size() > 0) {

            Long numberOfRecords = records.size() as Long
            Long totalExecutionTime = 0L

            getRecords().each { totalExecutionTime += it.runTimeInMilliseconds()}

            averageExecutionTime = totalExecutionTime / numberOfRecords
        }

        averageExecutionTime
    }

    Boolean isAlarmed() {

        final Boolean alarmed

        if (getRecords().size() < alarmThreshold) {

            alarmed = false
        } else {

            final List<DetailedPollResponse> scrutizedRecords

            if (getRecords().size() > alarmThreshold) {
                scrutizedRecords = Lists.partition(getRecords().reverse(), alarmThreshold).first()
            } else {
                scrutizedRecords = getRecords().reverse()
            }

            alarmed = scrutizedRecords.findAll { it.responseType != PollResponseType.success }.findAll { !it.cleared }.size() > 0
        }

        alarmed
    }

}
