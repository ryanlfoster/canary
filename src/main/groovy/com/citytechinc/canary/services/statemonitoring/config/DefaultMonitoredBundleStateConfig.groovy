package com.citytechinc.canary.services.statemonitoring.config

import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.PropertyOption
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
@Component(label = 'Canary Framework Monitored Bundle State Config', description = 'These configurations define bundles that will be monitored by the framework based on their state', configurationFactory = true, policy = ConfigurationPolicy.REQUIRE, metatype = true)
class DefaultMonitoredBundleStateConfig implements MonitoredBundleStateConfig {

    @Property(name = 'monitoredBundleName', label = 'Name', value = '', description = 'The bundle name that should be monitored')
    private String monitoredBundleName

    @Property(name = "monitoredBundleAlarmTimeout", label = 'Alarm Timeout', description = 'The alarm timeout value for a bundle in seconds',
        options = [
            @PropertyOption(name = "15 seconds", value = "15"),
            @PropertyOption(name = "30 seconds", value = "30"),
            @PropertyOption(name = "45 seconds", value = "45"),
            @PropertyOption(name = "60 seconds", value = "60")
        ])
    private Integer monitoredBundleAlarmTimeout

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {
        monitoredBundleName = PropertiesUtil.toString(properties.get('monitoredBundleName'), '')
        monitoredBundleAlarmTimeout = PropertiesUtil.toInteger(properties.get('monitoredBundleAlarmTimeout'), 0)
    }

    @Override
    String getMonitoredBundleName() {
        monitoredBundleName
    }

    @Override
    Integer getMonitoredBundleAlarmTimeout() {
        monitoredBundleAlarmTimeout
    }
}
