package com.citytechinc.canary.api.monitor

import com.google.common.base.Optional
import com.google.common.collect.Lists
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

    Queue<DetailedPollResponse> records = [] as Queue
    String monitorIdentifier
    Integer alarmThreshold
    Integer maxNumberOfRecords

    Integer lifetimeNumberOfPolls = 0
    Integer lifetimeNumberOfFailures = 0

    void addRecord(DetailedPollResponse record) {

        ++lifetimeNumberOfPolls

        if (record.responseType != PollResponseType.SUCCESS) {
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
        getRecords().count { it.responseType != PollResponseType.SUCCESS}
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

            alarmed = scrutinizedRecords.findAll { it.responseType != PollResponseType.SUCCESS }.findAll { !it.cleared }.size() > 0
        }

        alarmed
    }

}
