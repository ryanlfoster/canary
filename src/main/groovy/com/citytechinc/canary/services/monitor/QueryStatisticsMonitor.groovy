package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.citytechinc.canary.Constants
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Constants as OsgiConstants

import javax.management.MBeanServer
import javax.management.ObjectName
import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true)
@Service
@Properties(value = [
        @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@MonitoredServiceDefinition(description = 'Polls QueryStat bean', pollInterval = 1, pollIntervalUnit = TimeUnit.MINUTES, alarmThreshold = 10)
@AutomaticResetMonitor(resetInterval = 1, resetIntervalUnit = TimeUnit.MINUTES)
@Slf4j
class QueryStatisticsMonitor implements MonitoredService {

    private static final String BEAN = 'com.adobe.granite:type=TimeSeries,name=QUERY_AVERAGE,*'
    private static final String ATTRIBUTE = 'ValuePerSecond'

    @Reference
    MBeanServer server

    @Property(name = 'warningThreshold', label = 'Query time warning threshold', longValue = 10L, description = '')
    private Long warningThreshold

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {
        warningThreshold = PropertiesUtil.toLong(properties.get('warningThreshold'), 10L)
    }

    @Override
    PollResponse poll() {

        log.debug("Polling...")

        def response = PollResponse.SUCCESS()
        def names = server.queryNames(new ObjectName(BEAN), null)

        if (!names) {

            def timesSeriesMBean = names.first()
            def averageQueriesInMinutes = server.getAttribute(timesSeriesMBean, ATTRIBUTE) as List<Long>

            log.debug("averageQueriesInMinutes: ${averageQueriesInMinutes}")

            if (!averageQueriesInMinutes.findAll { it > warningThreshold }) {

                response = PollResponse.WARNING().addMessage("Highest average query time of: '${averageQueriesInMinutes.sort().last()}' exceeds threshold of: '${warningThreshold}'")
            }
        }

        response
    }
}
