package com.citytechinc.canary.components.dashboard

import com.citytechinc.canary.Constants
import com.citytechinc.canary.services.manager.ServiceManager
import com.citytechinc.canary.servlets.AbstractJSONResponseServlet
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

@SlingServlet(
        resourceTypes = Constants.DASHBOARD_COMPONENT_PATH,
        methods = 'GET',
        selectors = 'listmonitors',
        extensions = 'json')
class ListMonitors extends AbstractJSONResponseServlet {

    @Reference
    ServiceManager serviceManager

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {

        writeJsonResponse(response, serviceManager.listMonitors())
    }
}
