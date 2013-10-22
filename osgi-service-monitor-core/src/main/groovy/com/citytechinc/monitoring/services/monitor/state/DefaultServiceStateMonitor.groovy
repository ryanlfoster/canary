package com.citytechinc.monitoring.services.monitor.state

import com.citytechinc.monitoring.constants.Constants
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Service
import org.apache.sling.commons.osgi.OsgiUtil
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(configurationFactory = true, policy = ConfigurationPolicy.REQUIRE, label = 'Service State Monitor Definition', description = '', immediate = true, metatype = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
class DefaultServiceStateMonitor implements ServiceStateMonitor {

    @Property(label = 'Name of services to monitor', value = ['', ''], description = 'The fully qualified name of services to monitor')
    private static final String SERVICE_NAMES_TO_MONITOR = "serviceNamesToMonitor"
    private List<String> serviceNamesToMonitor

    @Activate
    @Modified
    public void activate(final Map<String, Object> properties) throws Exception {

        serviceNamesToMonitor = Arrays.asList(OsgiUtil.toStringArray(properties.get(SERVICE_NAMES_TO_MONITOR), []))
    }

    @Override
    List<String> getServiceName() {
        serviceNamesToMonitor as List<String>
    }
}
