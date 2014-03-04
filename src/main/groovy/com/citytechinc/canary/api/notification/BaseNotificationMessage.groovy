package com.citytechinc.canary.api.notification

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

    String monitorName
    String monitorDescription
    RecordHolder recordHolder
}
