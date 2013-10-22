package com.citytechinc.monitoring.services.persistence

import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties as SCRProperties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.adapter.AdapterFactory
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author CITYTECH, INC. 2013
 *
 */
@Component(immediate = true)
@Service(AdapterFactory)
@SCRProperties([
@Property(name = OsgiConstants.SERVICE_DESCRIPTION, value = "MyMove Address Express Domain Adapter"),
@Property(name = "adapters", value = ['com.imagitas.mymove.addressexpress.domain.jcr.Category', 'com.imagitas.mymove.addressexpress.domain.jcr.Company']),
@Property(name = "adaptables", value = ['org.apache.sling.api.resource.Resource'])])
@Slf4j
class MonitorRecordAdapterFactory {
}
