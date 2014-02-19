package com.citytechinc.canary.services.statemonitoring

import com.citytechinc.canary.Constants
import com.citytechinc.canary.services.manager.ServiceManager
import com.citytechinc.canary.services.statemonitoring.config.MonitoredBundleStateConfig
import com.citytechinc.canary.services.statemonitoring.config.MonitoredComponentStateConfig
import com.google.common.collect.Lists
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
class DefaultStateMonitorWatchdog implements StateMonitorWatchdog {

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = MonitoredBundleStateConfig,
            bind = 'bindMonitoredBundleStateConfig',
            unbind = 'unbindMonitoredBundleStateConfig')
    private List<MonitoredBundleStateConfig> monitoredBundles = Lists.newCopyOnWriteArrayList()

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = MonitoredComponentStateConfig,
            bind = 'bindMonitoredComponentStateConfig',
            unbind = 'unbindMonitoredComponentStateConfig')
    private List<MonitoredComponentStateConfig> monitoredComponents = Lists.newCopyOnWriteArrayList()

    void bindMonitoredBundleStateConfig(MonitoredBundleStateConfig monitoredBundleStateConfig) {
        monitoredBundles.add(monitoredBundleStateConfig)
    }

    void unbindMonitoredBundleStateConfig(MonitoredBundleStateConfig monitoredBundleStateConfig) {
        monitoredBundles.remove(monitoredBundleStateConfig)
    }

    void bindMonitoredComponentStateConfig(MonitoredComponentStateConfig monitoredComponentStateConfig) {
        monitoredComponents.add(monitoredComponentStateConfig)
    }

    void unbindMonitoredComponentStateConfig(MonitoredComponentStateConfig monitoredComponentStateConfig) {
        monitoredComponents.add(monitoredComponentStateConfig)
    }

    @Reference
    ServiceManager serviceManager
}
