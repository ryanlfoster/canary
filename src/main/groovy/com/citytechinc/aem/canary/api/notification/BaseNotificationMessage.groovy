package com.citytechinc.aem.canary.api.notification

import com.citytechinc.aem.canary.api.monitor.MonitorRecords
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

    String monitorName
    String monitorDescription
    MonitorRecords recordHolder
}
