package com.citytechinc.canary.services.monitor

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.google.common.collect.Maps
import groovy.util.logging.Slf4j
import groovyx.gpars.agent.Agent
import org.apache.felix.scr.ScrService
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.felix.scr.annotations.sling.SlingFilter
import org.apache.sling.api.SlingHttpServletRequest
import org.osgi.framework.Constants as OsgiConstants

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.Servlet
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
class SlingSelectorSentinelMonitor implements MonitoredService, Filter {

    static final SLING_SERVLET_RESOURCE_TYPES_CONFIG_KEY = 'sling.servlet.resourceTypes'
    static final SLING_SERVLET_SELECTORS_CONFIG_KEY = 'sling.servlet.selectors'

    private Agent requestStatistics = new RequestStatistics()

    @Reference
    ScrService scrService

    @Override
    PollResponse poll() {

        def statistics = requestStatistics.sendAndWait { retrieveAndClearStatistics() } as Map

        def messages = []

        statistics.each { requestURIs ->

            requestURIs.each { selectors ->

                messages.add("uri: ${requestURIs.key} has had selector: ${selectors.key} requested ${selectors.value} times".toString())
            }
        }

        messages ? PollResponse.WARNING().addMessages(messages) : PollResponse.SUCCESS()
    }

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        def slingRequest = servletRequest as SlingHttpServletRequest

        def requestSelectors = slingRequest.requestPathInfo.selectors as List<String>
        def requestResourceType = slingRequest.resource?.resourceType

        log.info("Processing request for resource type: ${requestResourceType} and selectors: ${requestSelectors}")

        def matchingResourceSelectors = []

        scrService.components.findAll { it.services?.contains(Servlet.name) }.each {

            def resourceTypes = it.properties[SLING_SERVLET_RESOURCE_TYPES_CONFIG_KEY] as String

            if (resourceTypes) {

                def splitResourceTypes = resourceTypes.minus('[').minus(']').minus(' ').split(',')

                if (splitResourceTypes.contains(requestResourceType)) {

                    def serviceSelectors = it.properties[SLING_SERVLET_SELECTORS_CONFIG_KEY] as String

                    if (serviceSelectors) {

                        def splitServiceSelectors = serviceSelectors.minus('[').minus(']').minus(' ').split(',')

                        matchingResourceSelectors.addAll(splitServiceSelectors)
                    }
                }
            }
        }

        def uniqueMatchingResourceSelectors = matchingResourceSelectors.unique()
        def uniqueRequestSelectors = requestSelectors.unique()

        def verifiedSelectors = uniqueRequestSelectors.intersect uniqueMatchingResourceSelectors
        def unverifiedSelectors = uniqueRequestSelectors - verifiedSelectors

        if (unverifiedSelectors) {

            log.info("The following selectors could be abusive: ${unverifiedSelectors}")

            unverifiedSelectors.each {

                requestStatistics << { recordRequest(slingRequest.requestURI, it) }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse)
    }

    @Override
    void destroy() {

    }

    class RequestStatistics extends Agent<Map<String, Map<String, Integer>>> {

        def RequestStatistics() {

            super([:])
        }

        private def recordRequest(String requestURI, String selector) {

            if (!data.containsKey(requestURI)) {

                data.put(requestURI, [:])
            }

            def innerMap = data.get(requestURI)
            def requestCount = innerMap.containsKey(selector) ? innerMap.get(selector) + 1 : 1

            innerMap.put(selector, requestCount)
        }

        private Map<String, Map<String, Integer>> retrieveAndClearStatistics() {

            def clonedMap = Maps.newHashMap(data)
            data.clear()

            clonedMap
        }
    }
}
