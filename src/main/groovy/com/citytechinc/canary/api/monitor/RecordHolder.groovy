package com.citytechinc.canary.api.monitor

import com.google.common.base.Optional
import com.google.common.collect.Lists
import com.google.common.collect.Queues
import groovy.transform.AutoClone
import groovy.transform.ToString
import groovy.util.logging.Slf4j

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@ToString(includeFields = true, includeNames = true)
@Slf4j
@AutoClone
class RecordHolder {

    final Queue<DetailedPollResponse> records = [] as Queue
    final String canonicalMonitorName
    final Integer alarmThreshold
    final Integer maxNumberOfRecords

    Integer lifetimeNumberOfPolls = 0
    Integer lifetimeNumberOfFailures = 0

    private RecordHolder(String canonicalMonitorName, Integer maxNumberOfRecords, Integer alarmThreshold) {
        this.canonicalMonitorName = canonicalMonitorName
        this.alarmThreshold = alarmThreshold
        this.maxNumberOfRecords = maxNumberOfRecords

        records = Queues.newArrayBlockingQueue(maxNumberOfRecords)
    }

    public static CREATE_NEW(MonitoredServiceWrapper wrapper) {

        return new RecordHolder(wrapper.identifier, wrapper.definition.maxPollHistoryEntries(), wrapper.definition.alarmThreshold())
    }

    public static CREATE_FROM_RECORDS(MonitoredServiceWrapper wrapper, List<DetailedPollResponse> records) {

        RecordHolder holder = new RecordHolder(wrapper.identifier, wrapper.definition.maxPollHistoryEntries(), wrapper.definition.alarmThreshold())
        records.each { holder.addRecord(it) }

        holder
    }

    void addRecord(DetailedPollResponse record) {

        ++lifetimeNumberOfPolls

        if (record.responseType != PollResponseType.success) {
            ++lifetimeNumberOfFailures
        }

        if (records.size() == maxNumberOfRecords) {
            records.remove()
        }

        records.offer(record)
    }

    void resetAlarm() {
        getRecords().last().cleared = true
    }

    List<DetailedPollResponse> getRecords() {
        records as List
    }

    Optional<Date> firstPoll() {

        records.empty ? Optional.absent() : Optional.of(records.peek().startTime)
    }

    Optional<Date> mostRecentPollDate() {

        records.empty ? Optional.absent() : Optional.of(getRecords().reverse().first().startTime)
    }

    Optional<PollResponseType> mostRecentPollResponse() {

        records.empty ? Optional.absent() : Optional.of(getRecords().reverse().first().responseType)
    }

    Integer recordNumberOfPolls() {
        records.size()
    }

    Integer recordNumberOfFailures() {
        getRecords().count { it.responseType != PollResponseType.success}
    }

    BigDecimal lifetimeFailureRate() {

        BigDecimal failureRate = new BigDecimal(lifetimeNumberOfFailures).divide(new BigDecimal(lifetimeNumberOfPolls))
        failureRate.movePointRight()
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

            final List<DetailedPollResponse> scrutinizedRecords

            if (getRecords().size() > alarmThreshold) {
                scrutinizedRecords = Lists.partition(getRecords().reverse(), alarmThreshold).first()
            } else {
                scrutinizedRecords = getRecords().reverse()
            }

            alarmed = scrutinizedRecords.findAll { it.responseType != PollResponseType.success }.findAll { !it.cleared }.size() > 0
        }

        alarmed
    }

}
