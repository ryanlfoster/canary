package com.citytechinc.canary.api.notification

import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.RecordHolder
import groovy.transform.ToString

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@ToString
class BaseNotificationMessage {

    MonitoredServiceDefinition monitorDefinition
    AutomaticResetMonitor automaticResetMonitor
    RecordHolder recordHolder
}
