package com.citytechinc.canary

import org.apache.commons.lang.time.FastDateFormat
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public final class Constants {

    static final CITYTECH_SERVICE_VENDOR_NAME = 'CITYTECH, Inc.'

    // COMMON DATES
    static final String JMX_DATE_TIME_DEFINITION = 'yyyy-MM-dd HH:mm:ss'
    static final FastDateFormat JMX_DATE_TIME_FORMATTER = FastDateFormat.getInstance(JMX_DATE_TIME_DEFINITION)
    static final DateTimeFormatter JMX_DATE_TIME_PARSER = DateTimeFormat.forPattern(JMX_DATE_TIME_DEFINITION)

    // JCR PERSISTENCE
    static final String JCR_POLL_RESPONSE_NODE_STORAGE_DEFINITION = 'yyyy-MM-dd-HH-mm-ss-SSS'
    static final FastDateFormat JCR_POLL_RESPONSE_NODE_STORAGE_FORMATTER = FastDateFormat.getInstance(JCR_POLL_RESPONSE_NODE_STORAGE_DEFINITION)

    static final String JCR_PERSISTENCE_STORAGE_ROOT_NODE = 'service_monitoring'
    static final String JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH = '/etc/' + JCR_PERSISTENCE_STORAGE_ROOT_NODE

    // NODE IDENTIFIERS
    static final String JCR_NODE_TYPE_MONITORED_SERVICE_STORAGE = 'canary:MonitorServiceStorage'
    static final String JCR_NODE_TYPE_RECORD_HOLDER = 'canary:MonitorRecords'
    static final String JCR_NODE_TYPE_DETAILED_POLL_RESPONSE = 'canary:PollResult'

    // ABSTRACT JSON RESPONSE SERVLET
    static final String ABSTRACT_JSON_RESPONSE_SERVLET_CONTENT_TYPE = 'application/json'
    static final String ABSTRACT_JSON_RESPONSE_SERVLET_CHARACTER_ENCODING = 'utf-8'
    static final String ABSTRACT_JSON_RESPONSE_SERVLET_DEFAULT_DATE_DEFINITION = 'yyyy-MM-dd\'T\'HH:mm:ss.SSSZ'
}
