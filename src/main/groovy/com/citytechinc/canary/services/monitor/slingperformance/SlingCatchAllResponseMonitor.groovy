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
@MonitoredServiceDefinition(description = 'Collects and reports on requests not covered by the dynamic and page response monitors that exceed the configured threshold', pollInterval = 30, pollIntervalUnit = TimeUnit.SECONDS)
@AutomaticResetMonitor(resetInterval = 30, resetIntervalUnit = TimeUnit.SECONDS)
@Slf4j
class SlingCatchAllResponseMonitor extends AbstractSlingResponseMonitor {

    @Override
    Logger getLogger() {
        log
    }

    @Override
    Boolean scrutinizeRequest(RequestPathInfo requestPathInfo) {

        ((!requestPathInfo.resourcePath.startsWith('/content') && !requestPathInfo.extension == 'html')
            || !['json', 'xml'].contains(requestPathInfo.extension))
    }
}
