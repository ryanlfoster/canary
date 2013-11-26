package com.citytechinc.monitoring.services.jcrpersistence

import com.citytechinc.monitoring.api.monitor.PollResponseType

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class ServiceMonitorRecord {

    Date startTime
    Date endTime
    PollResponseType responseType
    String stackTrace

}
