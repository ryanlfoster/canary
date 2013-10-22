package com.citytechinc.monitoring.services.persistence

import com.citytechinc.monitoring.services.monitor.PollResponseType

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class ServiceMonitorRecord {

    String monitoredService
    Date startTime
    Date endTime
    PollResponseType responseType
    String stackTrace

}
