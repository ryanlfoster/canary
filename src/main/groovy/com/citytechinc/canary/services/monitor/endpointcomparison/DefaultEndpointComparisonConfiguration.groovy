package com.citytechinc.canary.services.monitor.endpointcomparison

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
@Component(label = 'Canary Endpoint Comparison Monitor Configuration', description = '', configurationFactory = true, policy = ConfigurationPolicy.REQUIRE, metatype = true)
class DefaultEndpointComparisonConfiguration implements EndpointComparisonConfiguration {

    @Property(name = 'label', label = 'Label', value = '')
    private String label

    @Property(name = 'url', label = 'URL', value = '')
    private String url

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) throws Exception {

        label = PropertiesUtil.toString(properties.get('label'), '')
        url = PropertiesUtil.toString(properties.get('url'), '')
    }

    @Override
    String getLabel() {
        label
    }

    @Override
    String getURL() {
        url
    }
}
