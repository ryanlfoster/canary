package com.citytechinc.canary.services.monitor.endpointcomparison

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.day.cq.replication.ReplicationStatus
import com.google.common.collect.Lists
import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.PropertyOption
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Constants as OsgiConstants

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
@MonitoredServiceDefinition(description = 'Examines replication agents for blocked queues', pollInterval = 10, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 12)
@AutomaticResetMonitor(resetInterval = 3, resetIntervalUnit = TimeUnit.MINUTES)
class EndpointComparisonMonitor implements MonitoredService {

    static HashFunction HASH_FUNCTION = Hashing.goodFastHash(128)

    @Property(name = 'type', label = 'Comparison Type',
            options = [
            @PropertyOption(name = 'Random Page', value = 'RANDOM_PAGE'),
            @PropertyOption(name = 'Page From List', value = 'PAGE_FROM_LIST'),
            @PropertyOption(name = 'Root Server Path', value = 'ROOT_SERVER_PATH')])
    private ComparisonType type
    enum ComparisonType { RANDOM_PAGE, PAGE_FROM_LIST, ROOT_SERVER_PATH }

    @Property(name = 'randomPathStartPath', label = 'Random Page Start Path', value = '', description = 'Used if type is \'Random Page\', this represents the page path that will be used to randomly select a child for comparison')
    private String randomPathStartPath

    @Property(name = 'pagePaths', label = 'Path Paths to Hit', value = ['', ''], description = 'Used if type is \'Random Page\', this represents a list of page paths that will be used for comparison')
    private List<String> pagePaths

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = EndpointComparisonConfiguration,
            bind = 'bindEndpointComparisonConfiguration',
            unbind = 'unbindEndpointComparisonConfiguration')
    private List<EndpointComparisonConfiguration> endpointComparisonConfigurations = Lists.newCopyOnWriteArrayList()

    void bindEndpointComparisonConfiguration(EndpointComparisonConfiguration endpointComparisonConfiguration) {
        endpointComparisonConfigurations.add(endpointComparisonConfiguration)
    }

    void unbindEndpointComparisonConfiguration(EndpointComparisonConfiguration endpointComparisonConfiguration) {
        endpointComparisonConfigurations.remove(endpointComparisonConfiguration)
    }

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {

        randomPathStartPath = PropertiesUtil.toString(properties.get('randomPathStartPath'), '')
        type = ComparisonType.valueOf(PropertiesUtil.toString(properties.get('type'), 'ROOT_SERVER_PATH'))
        pagePaths = PropertiesUtil.toStringArray(properties.get('pagePaths')) as List
    }

    @Override
    PollResponse poll() {

        def random = new Random()
        def function = Hashing.goodFastHash(128)

        def pages = []

        getPage('/content/ajam').recurse { page ->
            if (page.contentResource.adaptTo(ReplicationStatus).activated) {
                pages.add(page.path)
            }
        }

        def pagePath = pages.get(random.nextInt(pages.size()))

        endpointComparisonConfigurations.each { config ->

        }

        scrutinzedURLs.sort { it.label }.each { it.url = it.url + pagePath + '.html' }.each {

            println "hash: ${function.hashBytes(it.url.toURL().getBytes())} for url ${it}"
        }
    }
}
