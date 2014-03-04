package com.citytechinc.canary.components.dashboard

import com.citytechinc.canary.services.manager.ServiceManager
import com.citytechinc.canary.services.manager.actors.Statistics
import com.citytechinc.canary.servlets.AbstractJSONResponseServlet
import com.google.common.base.Optional
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

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

        Optional<Statistics> statistics = serviceManager.getRecordHolder(request.getParameter('identifier'))
        writeJsonResponse(response, statistics.present ? statistics.get() : [])
    }
}
