package com.citytechinc.canary.services.manager.actors

import groovy.transform.AutoClone
import groovy.transform.ToString

@ToString(includeNames = true)
@AutoClone
class Statistics {

    Integer deliveredMessages = 0
    Integer processedMessages = 0
    Integer messageExceptions = 0
    Long averageMessageProcessTime = 0L

    void addAndCalculateAverageProcessTime(Long processTime) {

        if (processedMessages >= 2) {
            averageMessageProcessTime * (processedMessages - 1 / processedMessages) + processTime * (1 / processedMessages)
        } else {
            averageMessageProcessTime = processTime
        }
    }
}
