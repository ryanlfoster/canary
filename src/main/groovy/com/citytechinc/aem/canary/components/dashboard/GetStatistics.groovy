package com.citytechinc.aem.canary.components.dashboard

import com.citytechinc.aem.canary.services.manager.ServiceManager
import com.citytechinc.aem.canary.services.manager.actors.MissionControlActor
import com.citytechinc.aem.canary.servlets.AbstractJSONResponseServlet
import groovy.json.JsonBuilder
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@SlingServlet(
        resourceTypes = 'canary/components/page/dashboard',
        methods = 'GET',
        selectors = 'statistics',
        extensions = 'json')
class GetStatistics extends AbstractJSONResponseServlet {

    @Reference
    ServiceManager serviceManager

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {

        def statistics = serviceManager.getStatistics(request.getParameter('identifier'), MissionControlActor.GetStatistics.Type.valueOf(request.getParameter('type')))
        def jsonBuilder = new JsonBuilder(statistics.present ? statistics.get() : [])

        writeJsonResponse(response, jsonBuilder)
    }
}
