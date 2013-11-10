package com.citytechinc.monitoring.samples.responsehandler

import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.samples.monitor.NeverSuccessfulMonitor
import com.citytechinc.monitoring.services.monitor.PollResponse
import com.citytechinc.monitoring.services.notification.SubscriptionStrategy
import com.citytechinc.monitoring.services.responsehandler.PollResponseHandler
import com.citytechinc.monitoring.services.responsehandler.PollResponseHandlerDefinition
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
@PollResponseHandlerDefinition(subscriptionStrategy = SubscriptionStrategy.opt_out_of, subscriptionStrategySpecifics = [NeverSuccessfulMonitor])
class SpecificExcludeResponseLogger implements PollResponseHandler {

    @Override
    void handleResponse(String monitorName, PollResponse response) {

        log.info("Processing responses from all but NeverSuccesfulMonitor, this for monitor ${monitorName} and response ${response}")
    }
}
