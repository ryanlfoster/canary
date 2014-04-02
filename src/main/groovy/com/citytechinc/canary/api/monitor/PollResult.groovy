package com.citytechinc.canary.api.monitor

import groovy.transform.AutoClone
import groovy.transform.Canonical
import groovy.transform.ToString

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@ToString(includeNames = true, excludes = ['stackTrace'])
@AutoClone
@Canonical
class PollResult {

    Date startTime
    Date endTime
    PollResponseType responseType
    String stackTrace
    List<String> messages
    Boolean excused = false

    Long executionTimeInMilliseconds() {

        endTime.time - startTime.time
    }
}
