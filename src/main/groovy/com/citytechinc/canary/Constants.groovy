package com.citytechinc.canary

import org.apache.commons.lang.time.FastDateFormat

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public final class Constants {

    public static final String CITYTECH_SERVICE_VENDOR_NAME = 'CITYTECH, Inc.'

    public static final String JMX_DATE_TIME_DEFINITION = 'yyyy-MM-dd HH:mm:ss'
    public static final FastDateFormat JMX_DATE_TIME_FORMATTER = FastDateFormat.getInstance(JMX_DATE_TIME_DEFINITION)

    public static final String JCR_POLL_RESPONSE_NODE_STORAGE_DEFINITION = 'yyyy-MM-dd-HH-mm-ss-SSS'
    public static final FastDateFormat JCR_POLL_RESPONSE_NODE_STORAGE_FORMATTER = FastDateFormat.getInstance(JCR_POLL_RESPONSE_NODE_STORAGE_DEFINITION)

    public static final String SERVICE_MONITOR_DASHBOARD_PAGE_COMPONENT_PATH = '/apps/osgi-service-monitor/components/page/servicemonitordashboard'
    public static final String SERVICE_MONITOR_DASHBOARD_TEMPLATE_PATH = '/apps/osgi-service-monitor/templates/servicemonitordashboard'
    public static final String DASHBOARD_COMPONENT_PATH = 'osgi-service-monitor/components/content/dashboard'

    public static final String JCR_PERSISTENCE_STORAGE_ROOT_NODE = 'service_monitoring'
    public static final String JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH = '/etc/' + JCR_PERSISTENCE_STORAGE_ROOT_NODE
    public static final String JCR_NODE_TYPE_MONITORED_SERVICE_STORAGE = 'canary:MonitorServiceStorage'
    public static final String JCR_NODE_TYPE_DETAILED_POLL_RESPONSE = 'canary:DetailedPollResponse'
    public static final String JCR_NODE_TYPE_RECORD_HOLDER = 'canary:RecordHolder'

    public static final String ABSTRACT_JSON_RESPONSE_SERVLET_CONTENT_TYPE = 'application/json'
    public static final String ABSTRACT_JSON_RESPONSE_SERVLET_CHARACTER_ENCODING = 'utf-8'
    public static final String ABSTRACT_JSON_RESPONSE_SERVLET_DEFAULT_DATE_FORMAT = 'MM/dd/yyyy hh:mm aaa z'
}
