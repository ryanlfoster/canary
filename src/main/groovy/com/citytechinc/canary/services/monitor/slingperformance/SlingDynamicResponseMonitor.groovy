package com.citytechinc.canary.services.monitor.slingperformance

import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.sling.SlingFilter
import org.apache.sling.api.request.RequestPathInfo
import org.slf4j.Logger

import java.util.concurrent.TimeUnit

@SlingFilter(order = 1, generateService = false, generateComponent = false)
@Component
@MonitoredServiceDefinition(description = 'Collects and reports on dynamic (xml, json) requests that exceed the configured threshold', pollInterval = 30, pollIntervalUnit = TimeUnit.SECONDS)
@AutomaticResetMonitor(resetInterval = 30, resetIntervalUnit = TimeUnit.SECONDS)
@Slf4j
class SlingDynamicResponseMonitor extends AbstractSlingResponseMonitor {

    @Override
    Logger getLogger() {
        log
    }

    @Override
    Boolean scrutinizeRequest(RequestPathInfo requestPathInfo) {

        ['json', 'xml'].contains(requestPathInfo.extension)
    }
}
