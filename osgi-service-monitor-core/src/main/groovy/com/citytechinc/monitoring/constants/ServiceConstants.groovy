package com.citytechinc.monitoring.constants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class ServiceConstants {

    static final String JCR_PERSISTENCE_STORAGE_ROOT_NODE = 'service_monitoring'
    static final String JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH = '/etc/' + JCR_PERSISTENCE_STORAGE_ROOT_NODE
    static final String VENDOR_NAME = 'CITYTECH, Inc.'

    static final String JCR_NODE_TYPE_MONITORED_SERVICE_STORAGE = 'citytech:MonitorServiceStorage'
    static final String JCR_NODE_TYPE_DETAILED_POLL_RESPONSE = 'citytech:DetailedPollResponse'
    static final String JCR_NODE_TYPE_RECORD_HOLDER = 'citytech:RecordHolder'

    public static final String ABSTRACT_JSON_RESPONSE_SERVLET_CONTENT_TYPE = 'application/json'
    public static final String ABSTRACT_JSON_RESPONSE_SERVLET_CHARACTER_ENCODING = 'utf-8'
    public static final String ABSTRACT_JSON_RESPONSE_SERVLET_DEFAULT_DATE_FORMAT = 'MM/dd/yyyy hh:mm aaa z'

}
