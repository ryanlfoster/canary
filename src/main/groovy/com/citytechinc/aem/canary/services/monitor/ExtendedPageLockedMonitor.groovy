package com.citytechinc.aem.canary.services.monitor

import com.citytechinc.aem.canary.Constants
import com.citytechinc.aem.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.aem.canary.api.monitor.MonitoredService
import com.citytechinc.aem.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.aem.canary.api.monitor.PollResponse
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import groovy.util.logging.Slf4j
import groovyx.gpars.agent.Agent
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.felix.scr.annotations.sling.SlingFilter
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.Constants as OsgiConstants

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@SlingFilter(order = 1, generateService = false, generateComponent = false)
@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true)
@Service
@Properties(value = [
        @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@MonitoredServiceDefinition(description = '', pollInterval = 1, pollIntervalUnit = TimeUnit.MINUTES, alarmThreshold = 10)
@AutomaticResetMonitor(resetInterval = 1, resetIntervalUnit = TimeUnit.MINUTES)
@Slf4j
class ExtendedPageLockedMonitor implements MonitoredService, Filter {

    private Agent lockedPagesAgent = new LockedPagesAgent()

    @Property(name = 'rootRelevantPagePath', label = 'The root path for relevant pages.', description = 'If a locked/unlocked page does not start with this path, it is not considered.')
    private String rootRelevantPagePath

    @Property(name = 'pageLockTimeWarningThreshold', label = 'The page lock warning threshold time in minutes')
    private Long pageLockTimeWarningThreshold

    @Reference
    ResourceResolverFactory resourceResolverFactory

    @Activate
    void activate() {

        modified()

        // LOOK FOR LOCKED PAGES
        ResourceResolver resourceResolver = null
        try {

            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null)
            PageManager pageManager = resourceResolver.adaptTo(PageManager)

            pageManager.getPage(rootRelevantPagePath).recurse { Page page ->

                if (page.locked) {

                    lockedPagesAgent << { put(path, new Date()) }
                }
            }

        } finally {

            if (resourceResolver?.isLive()) {
                resourceResolver.close()
            }
        }
    }

    @Modified
    void modified(Map<String, Object> properties) throws Exception {
        rootRelevantPagePath = PropertiesUtil.toString(properties.get('rootRelevantPagePath'), '')
        pageLockTimeWarningThreshold = PropertiesUtil.toLong(properties.get('pageLockTimeWarningThreshold'), 0L)
    }

    @Override
    PollResponse poll() {

        log.debug("Polling...")

        def requestURIs = (requestStatistics.val as Map).keySet() as List

        log.debug("requestURIs: ${requestURIs}")

        def messages = []

        requestURIs.each { requestURI ->

            def map = requestStatistics.sendAndWait { it.remove(requestURI) } as Map

            map.each { selectorEntry ->

                messages.add("uri: ${requestURI} has had selector: ${selectorEntry.key} requested ${selectorEntry.value} times".toString())
            }
        }

        log.debug("messages: ${messages}")

        messages ? PollResponse.WARNING().addMessages(messages) : PollResponse.SUCCESS()
    }

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        def slingRequest = servletRequest as SlingHttpServletRequest

        if (slingRequest.pathInfo == '/bin/wcmcommand') {

            def command = slingRequest.parameterMap?.cmd?.first() as String
            def path = slingRequest.parameterMap?.path?.first() as String

            if (command) {

                if (command == 'unlockPage') {
                    lockedPagesAgent << { remove(path) }
                } else if (command == 'lockPage') {
                    lockedPagesAgent << { put(path, new Date()) }
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse)
    }

    @Override
    void destroy() {

    }

    class LockedPagesAgent extends Agent<Map<String, Date>> {

        def LockedPagesAgent() {

            super([:])
        }
    }
}