package com.citytechinc.canary.services.monitor.endpointcomparison

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.day.cq.replication.ReplicationStatus
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.google.common.base.Stopwatch
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.Actors
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
@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true, metatype = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@MonitoredServiceDefinition(description = 'Examines replication agents for blocked queues', pollInterval = 1, pollIntervalUnit = TimeUnit.MINUTES, alarmThreshold = 10)
@AutomaticResetMonitor(resetInterval = 1, resetIntervalUnit = TimeUnit.MINUTES)
@Slf4j
class EndpointComparisonMonitor implements MonitoredService {

    static HashFunction HASH_FUNCTION = Hashing.goodFastHash(128)
    static Random RANDOM_GENERATOR = new Random()

    @Property(name = 'type', label = 'Comparison Type', options = [
        @PropertyOption(name = 'RANDOM_PAGE', value = 'Random Page'),
        @PropertyOption(name = 'RANDOM_PAGE_FROM_LIST', value = 'Random Page From List'),
        @PropertyOption(name = 'ROOT_SERVER_PATH', value = 'Root Server Path')])
    private ComparisonType type
    enum ComparisonType { RANDOM_PAGE, RANDOM_PAGE_FROM_LIST, ROOT_SERVER_PATH }

    @Property(name = 'randomPathStartPath', label = 'Random Page Start Path', value = '', description = 'Used if type is \'Random Page\', this represents the page path that will be used to randomly select a child for comparison')
    private String randomPathStartPath

    @Property(name = 'pagePaths', label = 'Path Paths to Hit', value = ['', ''], description = 'Used if type is \'Random Page From List\', this represents a list of page paths that will be used for comparison')
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

                log.debug("rootPage= ${rootPage}")

                if (rootPage) {

                    List<String> descendantPathPaths = []

                    rootPage.recurse { page ->

                        log.debug("processing page: ${page}")

                        if (page.contentResource.adaptTo(ReplicationStatus).activated) {

                            log.debug("page: ${page} is activated")
                            descendantPathPaths.add(page.path)
                        }
                    }

                    if (descendantPathPaths) {
                        explicitPagePath = descendantPathPaths.get(RANDOM_GENERATOR.nextInt(descendantPathPaths.size())) + '.html'
                    }

                }

            } else if (type == ComparisonType.RANDOM_PAGE_FROM_LIST) {

                if (pagePaths) {

                    explicitPagePath = pagePaths.get(RANDOM_GENERATOR.nextInt(pagePaths.size()))
                }
            }

        } catch (Exception e) {

            log.error("An error occurred attempting to build page list for type: ${type}," +
                    " randomPathStartPath: ${randomPathStartPath}, pagePaths: ${pagePaths}", e)

        } finally {

            if (resourceResolver?.isLive()) {
                resourceResolver.close()
            }
        }

        def actors = []
        def results = Maps.newConcurrentMap()

        endpointComparisonConfigurations.each { configuration ->

            actors.add(Actors.actor {

                def stopWatch = Stopwatch.createStarted()

                String urlString = configuration.URL + explicitPagePath

                log.trace("Opening connection to url: ${urlString}")

                String urlHash = HASH_FUNCTION.hashBytes(urlString.toURL().getBytes()).toString()

                log.info("Connection response received for url: ${urlString}, hash: ${urlHash} in ${stopWatch.stop().elapsed(TimeUnit.MILLISECONDS)}ms")

                results.put(configuration, urlHash)
            })
        }

        actors*.join()

        (results.values() as List).unique() == 1 ? PollResponse.SUCCESS() : PollResponse.WARNING().addMessages(results.collect {

            "${it.key.URL + explicitPagePath} had a hash of ${it.value}" as String
        })
    }
}
