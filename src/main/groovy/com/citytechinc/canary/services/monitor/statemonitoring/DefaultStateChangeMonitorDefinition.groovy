package com.citytechinc.canary.services.monitor.statemonitoring

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
class DefaultStateChangeMonitorDefinition implements StateChangeMonitorDefinition {

    @Property(name = 'bundleName', label = 'Bundle Symbolic Name', value = '', description = 'The bundle name that should be monitored')
    private String bundleName

    @Property(name = 'policy', label = 'Alarm Timeout', description = 'The alarm timeout value for a bundle in seconds',
            options = [
            @PropertyOption(name = 'All Components', value = 'ALL_COMPONENTS'),
            @PropertyOption(name = 'All but the Listed Components', value = 'ALL_BUT_LISTED_COMPONENTS'),
            @PropertyOption(name = 'Only the Listed Components', value = 'ONLY_LISTED_COMPONENTS')])
    private String policy

    @Property(name = 'specifics', label = 'Component names', value = ['', ''], description = 'Specific component names to be included or excluded from monitoring')
    private List<String> specifics

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {
        bundleName = PropertiesUtil.toString(properties.get('bundleName'), '')
        policy = PropertiesUtil.toInteger(properties.get('policy'), 0)
        specifics = PropertiesUtil.toStringArray(properties.get(specifics)) as List
    }

    @Override
    String getBundleName() {
        bundleName
    }

    @Override
    String getPolicy() {
        policy
    }

    @Override
    List<String> getSpecifics() {
        specifics
    }
}
