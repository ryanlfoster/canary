package com.citytechinc.canary

import com.citytechinc.canary.api.monitor.AlarmCriteria
import com.citytechinc.canary.api.monitor.PollResult
import com.citytechinc.canary.api.monitor.PollResponseType
import com.citytechinc.canary.api.monitor.MonitorRecords
import spock.lang.Specification

class RecordHolderSpecification extends Specification {

    def "Test total failure rate, after alarmed, after reset"() {

    }

    def "Test unexcused failure rate, after alarmed, after reset"() {

    }

    def "Test total failure rate"() {

    }

    def "Test average execution time, after alarmed, after reset"() {

    }

    def "Test unexcused average execution time, after alarmed, after reset"() {

    }

    def "Test average execution time"() {

    }

    def "Test number of records, unexcused records, most recent poll date and status, number of polls, number of failures"() {

    }

    def "Test average failure rate alarm criteria alarmed after threshold"() {

    }

    def "Test average execution time alarm criteria alarmed after threshold"() {

    }

    def "Test recent polls alarm criteria alarmed after threshold"() {

        MonitorRecords recordHolder = new MonitorRecords('com.a.b.c', 10, AlarmCriteria.RECENT_POLLS, 3, [])

        when:
        3.times {
            recordHolder.addRecord(new PollResult(startTime: new Date(), endTime: new Date(), responseType: PollResponseType.SUCCESS))
        }

        then:
        !recordHolder.isAlarmed()

        when:
        2.times {
            recordHolder.addRecord(new PollResult(startTime: new Date(), endTime: new Date(), responseType: PollResponseType.ERROR))
        }

        then:
        !recordHolder.isAlarmed()

        when:
        recordHolder.addRecord(new PollResult(startTime: new Date(), endTime: new Date(), responseType: PollResponseType.SUCCESS))

        then:
        !recordHolder.isAlarmed()

        when:
        3.times {
            recordHolder.addRecord(new PollResult(startTime: new Date(), endTime: new Date(), responseType: PollResponseType.ERROR))
        }

        then:
        recordHolder.isAlarmed()
    }
}
