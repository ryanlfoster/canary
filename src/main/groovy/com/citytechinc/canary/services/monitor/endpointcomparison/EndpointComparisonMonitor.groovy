package com.citytechinc.canary.services.monitor.endpointcomparison

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.day.cq.replication.ReplicationStatus
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing
import groovy.util.logging.Slf4j
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.PGroup
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
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
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
@Component(name = 'Canary Endpoint Comparison Monitor', policy = ConfigurationPolicy.REQUIRE, immediate = true, metatype = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@MonitoredServiceDefinition(description = 'Examines replication agents for blocked queues', pollInterval = 10, pollIntervalUnit = TimeUnit.SECONDS, alarmThreshold = 12)
@AutomaticResetMonitor(resetInterval = 3, resetIntervalUnit = TimeUnit.MINUTES)
@Slf4j
class EndpointComparisonMonitor implements MonitoredService {

    static HashFunction HASH_FUNCTION = Hashing.goodFastHash(128)
    static Random RANDOM_GENERATOR = new Random()

    @Property(name = 'type', label = 'Comparison Type', options = [
        @PropertyOption(name = 'Random Page', value = 'RANDOM_PAGE'),
        @PropertyOption(name = 'Random Page From List', value = 'RANDOM_PAGE_FROM_LIST'),
        @PropertyOption(name = 'Root Server Path', value = 'ROOT_SERVER_PATH')])
    private ComparisonType type
    enum ComparisonType { RANDOM_PAGE, RANDOM_PAGE_FROM_LIST, ROOT_SERVER_PATH }

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

    @Reference
    private ResourceResolverFactory resourceResolverFactory

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

        String explicitPagePath = ''

        ResourceResolver resourceResolver = null

        try {

            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null)
            PageManager pageManager = resourceResolver.adaptTo(PageManager)

            if (type == ComparisonType.RANDOM_PAGE && randomPathStartPath) {

                Page rootPage = pageManager.getPage(randomPathStartPath)

                if (rootPage) {

                    List<String> descendantPathPaths = []

                    rootPage.recurse { page ->

                        if (page.contentResource.adaptTo(ReplicationStatus).activated) {
                            descendantPathPaths.add(page.path)
                        }
                    }

                    explicitPagePath = descendantPathPaths.get(RANDOM_GENERATOR.nextInt(descendantPathPaths.size())) + '.html'
                }

            } else if (type == ComparisonType.RANDOM_PAGE_FROM_LIST) {

                List<String> activePages = []

                if (pagePaths.empty) {

                    log.debug("Random page paths was the selected configuration but no paths were supplied")

                } else {

                    pagePaths.each { pagePath ->

                        if (pageManager.getPage(pagePath) && pageManager.getPage(pagePath).adaptTo(ReplicationStatus).activated) {

                            activePages.add(pagePath)
                        }
                    }

                    if (activePages.empty) {

                        log.debug("Looked up ${pagePaths.size()} pages but none were found and active")
                    }
                }

                explicitPagePath = activePages.get(RANDOM_GENERATOR.nextInt(activePages.size())) + '.html'
            }

        } catch (Exception e) {

            log.error("An error occurred attempting to build page list for type: ${type}," +
                    " randomPathStartPath: ${randomPathStartPath}, pagePaths: ${pagePaths}", e)

        } finally {

            if (resourceResolver && resourceResolver.isLive()) {
                resourceResolver.close()
            }
        }

        PGroup group = new DefaultPGroup()
        Map<EndpointComparisonConfiguration, String> results = Maps.newConcurrentMap()

        endpointComparisonConfigurations.each { configuration ->

            group.actor {

                String urlString = configuration.URL + explicitPagePath

                log.info("Opening connection to url: ${urlString}")

                String urlHash = HASH_FUNCTION.hashBytes(urlString.toURL().getBytes()).toString()

                log.info("Connection response received for url: ${urlString}, hash: ${urlHash}")

                results.put(configuration, urlHash)
            }
        }

        PollResponse.SUCCESS()
    }
}
