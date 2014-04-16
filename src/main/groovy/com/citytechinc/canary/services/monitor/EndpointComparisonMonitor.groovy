package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.day.cq.replication.ReplicationStatus
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.google.common.base.Stopwatch
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
@MonitoredServiceDefinition(description = 'Reaches out to external endpoints and compares their responses with a hash and reports if they are equal', pollInterval = 1, pollIntervalUnit = TimeUnit.MINUTES, alarmThreshold = 10, maxExecutionTime = 10000L)
@AutomaticResetMonitor(resetInterval = 1, resetIntervalUnit = TimeUnit.MINUTES)
@Slf4j
class EndpointComparisonMonitor implements MonitoredService {

    static HashFunction HASH_FUNCTION = Hashing.goodFastHash(128)
    static Random RANDOM_GENERATOR = new Random()

    @Property(name = 'endPoints', label = 'End Points to Hit', value = ['', ''], description = 'List of end points to hit with no trailing slash and no supplied resource. ex: \'http://HOST:PORT\'')
    private List<String> endPoints

    @Property(name = 'type', label = 'Comparison Type', options = [
        @PropertyOption(name = 'RANDOM_PAGE', value = 'Random Page'),
        @PropertyOption(name = 'RANDOM_PAGE_FROM_LIST', value = 'Random Page From List'),
        @PropertyOption(name = 'ROOT_SERVER_PATH', value = 'Root Server Path')])
    private ComparisonType type
    enum ComparisonType { RANDOM_PAGE, RANDOM_PAGE_FROM_LIST, ROOT_SERVER_PATH }

    @Property(name = 'rootRandomPagePath', label = 'Root Random Page Page', value = '', description = 'The root page used to produce a list of descendants and randomly select from for comparison')
    private String rootRandomPagePath

    @Property(name = 'resourcePaths', label = 'Resource Paths to Hit', value = ['', ''], description = 'List of absolute resource paths to randomly select from. ex: \'/content/yoursite.html\'')
    private List<String> resourcePaths

    @Reference
    private ResourceResolverFactory resourceResolverFactory

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {

        endPoints = PropertiesUtil.toStringArray(properties.get('endPoints')) as List
        rootRandomPagePath = PropertiesUtil.toString(properties.get('rootRandomPagePath'), '')
        type = ComparisonType.valueOf(PropertiesUtil.toString(properties.get('type'), 'ROOT_SERVER_PATH'))
        resourcePaths = PropertiesUtil.toStringArray(properties.get('resourcePaths')) as List
    }

    @Override
    PollResponse poll() {

        log.debug("Polling...")

        String explicitPagePath = ''

        ResourceResolver resourceResolver = null

        try {

            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null)
            PageManager pageManager = resourceResolver.adaptTo(PageManager)

            if (type == ComparisonType.RANDOM_PAGE && rootRandomPagePath) {

                Page rootPage = pageManager.getPage(rootRandomPagePath)

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

                if (resourcePaths) {

                    explicitPagePath = resourcePaths.get(RANDOM_GENERATOR.nextInt(resourcePaths.size()))
                }
            }

        } catch (Exception e) {

            log.error("An error occurred attempting to build page list for type: ${type}," +
                    " rootRandomPagePath: ${rootRandomPagePath}, resourcePaths: ${resourcePaths}", e)

        } finally {

            if (resourceResolver?.isLive()) {
                resourceResolver.close()
            }
        }

        def actors = []
        def results = Maps.newConcurrentMap()

        actors = endPoints.collect {

            Actors.actor {

                def stopWatch = Stopwatch.createStarted()
                String urlString = it + explicitPagePath

                log.trace("Opening connection to url: ${urlString}")

                def urlHash = HASH_FUNCTION.hashBytes(urlString.toURL().getBytes()).toString()

                log.info("Connection response received for url: ${urlString}, hash: ${urlHash} in ${stopWatch.stop().elapsed(TimeUnit.MILLISECONDS)}ms")

                results.put(urlString, urlHash)
            }
        }

        actors*.join()

        if (results.isEmpty() || (results.values() as List).unique().size() == 1) {

            PollResponse.SUCCESS()
        } else {

            PollResponse.WARNING().addMessages(results.collect {

                "${it.key} had a hash of ${it.value}" as String
            })
        }
    }
}
