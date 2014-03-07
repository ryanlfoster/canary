package com.citytechinc.canary.services.monitor.endpointcomparison

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
@Component(label = 'Endpoint Comparison Monitor Configuration', description = '', configurationFactory = true, policy = ConfigurationPolicy.REQUIRE, metatype = true)
class DefaultEndpointComparisonConfiguration implements EndpointComparisonConfiguration {

    @Property(name = 'label', label = 'Label', value = '')
    private String label

    @Property(name = 'type', label = 'Type',
            options = [
            @PropertyOption(name = 'Load Balancer', value = 'LOAD_BALANCER'),
            @PropertyOption(name = 'Application Server', value = 'APPSERVER'),
            @PropertyOption(name = 'Web Server', value = 'WEBSERVER')])
    private EndpointComparisonConfiguration.TargetType type

    @Property(name = 'url', label = 'URL', value = '')
    private String url

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {

        label = PropertiesUtil.toString(properties.get('label'), '')
        type = EndpointComparisonConfiguration.TargetType.valueOf(PropertiesUtil.toString(properties.get('type'), 'LOAD_BALANCER'))
        url = PropertiesUtil.toString(properties.get('url'), '')
    }

    @Override
    String getLabel() {
        label
    }

    @Override
    EndpointComparisonConfiguration.TargetType getType() {
        type
    }

    @Override
    String getURL() {
        url
    }
}
