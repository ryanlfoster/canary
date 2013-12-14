package com.citytechinc.monitoring.samples.responsehandler

import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.api.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Service
@Properties(value = [
@Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
class ResponseLogger implements PollResponseHandler {

    @Override
    void handleResponse(DetailedPollResponse serviceMonitorRecord) {

    }
}
