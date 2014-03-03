package com.citytechinc.canary.services.monitor.statemonitoring

import com.citytechinc.canary.Constants
import com.citytechinc.canary.api.monitor.MonitoredService
import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
import com.citytechinc.canary.api.monitor.PollResponse
import com.google.common.collect.Lists
import org.apache.felix.scr.ScrService
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@MonitoredServiceDefinition(description = 'Monitors bundles/components for state changes', pollInterval = 3, pollIntervalUnit = TimeUnit.MINUTES, alarmThreshold = 10)
class StateChangeMonitor implements MonitoredService {

    @Reference
    ScrService scrService

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = StateChangeMonitorDefinition,
            bind = 'bindStateChangeMonitorDefinition',
            unbind = 'unbindStateChangeMonitorDefinition')
    private List<StateChangeMonitorDefinition> stateChangeMonitorDefinitions = Lists.newCopyOnWriteArrayList()

    void bindStateChangeMonitorDefinition(StateChangeMonitorDefinition stateChangeMonitorDefinition) {
        stateChangeMonitorDefinitions.add(stateChangeMonitorDefinition)
    }

    void unbindStateChangeMonitorDefinition(StateChangeMonitorDefinition stateChangeMonitorDefinition) {
        stateChangeMonitorDefinitions.remove(stateChangeMonitorDefinition)
    }

    @Override
    PollResponse poll() {

        PollResponse.SUCCESS()
    }
}
