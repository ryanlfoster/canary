package com.citytechinc.monitoring.services.adapters

import com.citytechinc.monitoring.api.monitor.PollResponseType
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ValueMap
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Service(AdapterFactory)
@Properties([
    @Property(name = OsgiConstants.SERVICE_DESCRIPTION, value = "Monitor Record Adapter Factory"),
    @Property(name = "adapters", value = ['com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse']),
    @Property(name = "adaptables", value = ['org.apache.sling.api.resource.Resource'])])
@Slf4j
class MonitorRecordAdapterFactory implements AdapterFactory {

    @Override
    def <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> adapterTypeClass) {

        final AdapterType result

        final Resource resource = (Resource)adaptable
        final ValueMap properties = resource.adaptTo(ValueMap)

        def record = new DetailedPollResponse()

        record.monitoredService = properties.get('monitoredService')
        record.startTime = properties.get('startTime')
        record.endTime = properties.get('endTime')
        record.responseType = PollResponseType.valueOf(properties.get('responseType'))
        record.stackTrace = properties.get('stackTrace')

        (AdapterType) record
    }
}
