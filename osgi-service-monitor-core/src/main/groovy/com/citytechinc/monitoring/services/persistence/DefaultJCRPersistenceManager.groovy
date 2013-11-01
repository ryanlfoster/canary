package com.citytechinc.monitoring.services.persistence

import com.citytechinc.monitoring.constants.ServiceConstants
import com.citytechinc.monitoring.services.manager.ServiceMonitorRecordHolder
import com.day.cq.commons.jcr.JcrConstants
import com.google.common.collect.Lists
import groovy.util.logging.Slf4j
import groovyx.gpars.GParsPool
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.ConfigurationPolicy
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
@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true)
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

        if (!session.nodeExists(ServiceConstants.JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH)) {

            def node = session.getNode('/etc')
            node.addNode(ServiceConstants.JCR_PERSISTENCE_STORAGE_ROOT_NODE, JcrConstants.NT_FOLDER)

            session.save()
            session.logout()
        }
    }

    @Override
    void persistRecords(List<ServiceMonitorRecordHolder> records) {

        GParsPool.withPool {

            records.eachParallel { recordHolder ->

                def session

                try {

                    session = slingRepository.loginAdministrative(null)

                    if (session.nodeExists(ServiceConstants.JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH + '/' + recordHolder.monitoredService)) {

                        def nodeToRemove = session.getNode(session.nodeExists(ServiceConstants.JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH + '/' + recordHolder.monitoredService))
                        nodeToRemove.remove()
                        session.save()
                    }

                    def rootNode = session.getNode(ServiceConstants.JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH)
                    def recordHolderNode = rootNode.addNode(recordHolder.monitoredService, ServiceConstants.MONITOR_RECORD_HOLDER_NODE_TYPE)

                    recordHolder.getRecords().each { record ->

                        def recordNode = recordHolderNode.addNode(record.startTime, ServiceConstants.MONITOR_RECORD_NODE_TYPE)

                        recordNode.set('startTime', record.startTime)
                        recordNode.set('endTime', record.endTime)
                        recordNode.set('responseType', record.responseType)
                        recordNode.set('stackTrace', record.stackTrace)
                    }

                    session.save()

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

        def session = slingRepository.loginAdministrative(null)
        def rootNode = session.getNode(ServiceConstants.JCR_PERSISTENCE_STORAGE_ROOT_NODE)

        def collectedRecords = Lists.newCopyOnWriteArrayList()

        GParsPool.withPool {

            rootNode.recurse
        }

        session.logout()
    }
}
