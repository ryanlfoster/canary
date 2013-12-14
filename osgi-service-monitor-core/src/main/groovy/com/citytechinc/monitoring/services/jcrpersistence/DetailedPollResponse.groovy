package com.citytechinc.monitoring.services.jcrpersistence

import com.citytechinc.monitoring.api.monitor.PollResponseType
import groovy.transform.ToString

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@ToString
class DetailedPollResponse {

    Date startTime
    Date endTime
    PollResponseType responseType
    String stackTrace
    String monitoredServiceClassname

}
