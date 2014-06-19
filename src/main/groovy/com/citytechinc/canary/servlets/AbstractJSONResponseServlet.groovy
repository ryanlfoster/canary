package com.citytechinc.canary.servlets

import com.citytechinc.canary.Constants
import groovy.util.logging.Slf4j
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Slf4j
public abstract class AbstractJSONResponseServlet extends SlingAllMethodsServlet {

    /**
     * Write an object to the response as JSON.
     *
     * @param response sling response
     * @param object object to be written as JSON
     */
    protected final void writeJsonResponse(final SlingHttpServletResponse response, final Object object) {

        response.setContentType(Constants.ABSTRACT_JSON_RESPONSE_SERVLET_CONTENT_TYPE)
        response.setCharacterEncoding(Constants.ABSTRACT_JSON_RESPONSE_SERVLET_CHARACTER_ENCODING)
    }
}
