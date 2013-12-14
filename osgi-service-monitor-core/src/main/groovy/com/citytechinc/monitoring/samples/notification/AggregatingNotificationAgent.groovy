package com.citytechinc.monitoring.samples.notification

import com.citytechinc.monitoring.api.notification.AggregateAlarms
import com.citytechinc.monitoring.api.notification.NotificationAgent
import com.citytechinc.monitoring.api.notification.NotificationAgentDefinition
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.felix.scr.annotations.Properties
import org.osgi.framework.Constants

import java.util.concurrent.TimeUnit

/**
 * Created by jdurbin on 12/14/13.
 */
@Component(immediate = true)
@Service
@Properties(value = [
    @Property(name = Constants.SERVICE_VENDOR, value = com.citytechinc.monitoring.constants.Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@NotificationAgentDefinition()
@AggregateAlarms(aggregationWindow = 30, aggregationWindowTimeUnit = TimeUnit.SECONDS)
class AggregatingNotificationAgent implements NotificationAgent {

    @Override
    void notify(List<ServiceMonitorRecordHolder> recordHolders) {

        log.info("Handling notififcation for ${recordHolders.size()} records")
    }
}
