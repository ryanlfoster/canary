package com.citytechinc.aem.canary.components.dashboard

import com.citytechinc.aem.canary.api.monitor.MonitorRecords
import com.citytechinc.aem.canary.services.manager.ServiceManager
import com.citytechinc.aem.canary.servlets.AbstractJSONResponseServlet
import com.google.common.base.Optional
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
        selectors = 'records',
        extensions = 'json')
class GetRecords extends AbstractJSONResponseServlet {

    @Reference
    ServiceManager serviceManager

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {

        Optional<MonitorRecords> record = serviceManager.getRecordHolder(request.getParameter('identifier'))
        writeJsonResponse(response, record.present ? record.get() : [])
    }
}
