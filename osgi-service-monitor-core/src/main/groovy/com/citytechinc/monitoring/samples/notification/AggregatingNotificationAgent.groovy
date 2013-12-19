package com.citytechinc.monitoring.samples.notification

import com.citytechinc.monitoring.api.notification.AggregateAlarms
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.api.notification.NotificationAgentDefinition
import com.citytechinc.monitoring.constants.Constants
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.felix.scr.annotations.Properties
import org.osgi.framework.Constants as OsgiConstants

import java.util.concurrent.TimeUnit

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
@NotificationAgentDefinition()
@AggregateAlarms(aggregationWindow = 30, aggregationWindowTimeUnit = TimeUnit.SECONDS)
class AggregatingNotificationAgent implements NotificationAgent {

    @Override
    void notify(List<ServiceMonitorRecordHolder> recordHolders) {

        log.info("Handling notififcation for ${recordHolders.size()} records")
    }
}
