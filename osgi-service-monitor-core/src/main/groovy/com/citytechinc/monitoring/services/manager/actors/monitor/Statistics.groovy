package com.citytechinc.monitoring.services.manager.actors.monitor

import groovy.transform.AutoClone
import groovy.transform.ToString

@ToString(includeNames = true)
@AutoClone
class Statistics {

    Integer numberOfProcessedMessages = 0
    Long averageMessageProcessTime = 0L
    Integer numberOfMessageExceptions = 0

    void addProcessTime(Long processTime) {

        if (numberOfProcessedMessages >= 2) {
            averageMessageProcessTime * (numberOfProcessedMessages - 1 / numberOfProcessedMessages) + processTime * (1 / numberOfProcessedMessages)
        } else {
            averageMessageProcessTime = processTime
        }
    }
}
