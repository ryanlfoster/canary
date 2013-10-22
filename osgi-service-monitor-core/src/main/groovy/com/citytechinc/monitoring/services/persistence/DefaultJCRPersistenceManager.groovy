package com.citytechinc.monitoring.services.persistence

import com.citytechinc.monitoring.constants.ServiceConstants
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import com.day.cq.commons.jcr.JcrUtil
import groovy.util.logging.Slf4j
import groovyx.gpars.GParsExecutorsPool
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.jcr.api.SlingRepository
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(label = 'OSGi Service Monitor Default JCR Persistence Manager', description = 'Responsible for persisting monitor data to the JCR.', immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = ServiceConstants.VENDOR_NAME) ])
@Slf4j
@RecordPersistenceServiceDefinition(ranking = 10)
class DefaultJCRPersistenceManager implements RecordPersistenceService {

    @Reference
    SlingRepository slingRepository

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws Exception {

        def session = slingRepository.loginAdministrative(null)

        if (!session.nodeExists(ServiceConstants.JCR_PERSISTENCE_STORAGE_ROOT_NODE)) {

            JcrUtil.createPath(ServiceConstants.JCR_PERSISTENCE_STORAGE_ROOT_NODE, 'nt:unstructured', 'nt:unstructured', session, false)

            session.save()
            session.logout()
        }
    }

    @Override
    void persistRecords(List<ServiceMonitorRecordHolder> records) {

        GParsExecutorsPool.withPool {

            records.eachParallel { recordHolder ->

                def session

                try {

                    session = slingRepository.loginAdministrative(null)

                    recordHolder.getRecordsAsList().each { record ->

                        def recordNode = JcrUtil.createPath(record.monitoredService, 'nt:unstructured', ServiceConstants.MONITOR_RECORD_NODE_TYPE, session, false)

                        recordNode.set('monitoredService', record.monitoredService)
                        recordNode.set('startTime', record.startTime)
                        recordNode.set('endTime', record.endTime)
                        recordNode.set('responseType', record.responseType)
                        recordNode.set('stackTrace', record.stackTrace)
                    }

                } catch (all) {
                    log.error(all)
                } finally {
                    session.logout()
                }
            }
        }
    }

    @Override
    List<ServiceMonitorRecordHolder> loadRecords() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}
