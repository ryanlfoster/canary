package com.citytechinc.canary.services.statemonitoring.config

import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.PropertiesUtil

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Service
@Component(label = 'Canary Framework Monitored Component State Config', description = 'These configurations defined components that will be monitored by the framework based on their state', configurationFactory = true, policy = ConfigurationPolicy.REQUIRE, metatype = true)
class DefaultMonitoredComponentStateConfig implements MonitoredComponentStateConfig {

    @Property(name = 'monitoredComponentName', label = 'Name', value = '', description = 'The component name that should be monitored')
    private String monitoredComponentName

    @Property(name = 'monitoredComponentAlarmTimeout', label = 'Alarm Timeout', intValue = 30, description = 'The alarm timeout value for a component in seconds')
    private Integer monitoredComponentAlarmTimeout

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {
        monitoredComponentName = PropertiesUtil.toString(properties.get('monitoredComponentName'), '')
        monitoredComponentAlarmTimeout = PropertiesUtil.toInteger(properties.get('monitoredComponentAlarmTimeout'), 0)
    }

    @Override
    String getMonitoredComponentName() {
        monitoredComponentName
    }

    @Override
    Integer getMonitoredComponentAlarmTimeout() {
        monitoredComponentAlarmTimeout
    }
}
