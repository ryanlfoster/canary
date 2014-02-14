package com.citytechinc.canary.api.monitor

import groovy.transform.AutoClone
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@ToString(includeNames = true, excludes = ['stackTrace'])
@Immutable
@AutoClone
class DetailedPollResponse {

    Date startTime
    Date endTime
    PollResponseType responseType
    String stackTrace
    Boolean excused = false

    Long executionTimeInMilliseconds() {

        endTime.time - startTime.time
    }
}
