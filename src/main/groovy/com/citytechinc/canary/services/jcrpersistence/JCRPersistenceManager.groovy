package com.citytechinc.canary.services.jcrpersistence

import com.citytechinc.canary.api.persistence.RecordPersistenceService
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceDefinition
import com.citytechinc.canary.constants.Constants
import com.google.common.base.Optional
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.jcr.api.SlingRepository
import org.osgi.framework.Constants as OsgiConstants

import javax.jcr.Session

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
//@Component(policy = ConfigurationPolicy.REQUIRE, immediate = true)
@Component(immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
@RecordPersistenceServiceDefinition(ranking = 10)
class JCRPersistenceManager implements RecordPersistenceService {

    @Reference
    SlingRepository slingRepository

    @Reference
    ResourceResolverFactory resolverFactory

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) throws Exception {

        Session session = slingRepository.loginAdministrative(null)

        if (!session.nodeExists(Constants.JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH)) {

            def node = session.getNode('/etc')
            node.addNode(Constants.JCR_PERSISTENCE_STORAGE_ROOT_NODE, Constants.JCR_NODE_TYPE_MONITORED_SERVICE_STORAGE)

            session.save()
        }

        session.logout()
    }

    @Override
    void persistRecordHolder(RecordHolder recordHolder) {

        log.debug("Persiting data for monitor: ${recordHolder.canonicalMonitorName}")

        def session

        try {

            session = slingRepository.loginAdministrative(null)

            def nodePath = Constants.JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH + '/' + recordHolder.canonicalMonitorName

            if (session.nodeExists(nodePath)) {

                def node = session.getNode(nodePath)
                node.remove()
                session.save()

                log.debug("Removed node ${nodePath} to make room for new record set")
            }

            def rootStorageNode = session.getNode(Constants.JCR_PERSISTENCE_STORAGE_ROOT_NODE_PATH)

            // CREATE A NEW NODE FOR THE MONITOR
            def recordHolderNode = rootStorageNode.addNode(recordHolder.canonicalMonitorName, Constants.JCR_NODE_TYPE_RECORD_HOLDER)

            recordHolderNode.set('lifetimeAverageProcessTime', 0L)
            recordHolderNode.set('lifetimeNumberOfPolls', recordHolder.lifetimeNumberOfPolls as Long)
            recordHolderNode.set('lifetimeNumberOfFailures', recordHolder.lifetimeNumberOfFailures as Long)

            recordHolder.records.each { DetailedPollResponse pollResponse ->

                def pollResponseNode = recordHolderNode.addNode(Constants.JCR_POLL_RESPONSE_NODE_STORAGE_FORMATTER.format(pollResponse.startTime), Constants.JCR_NODE_TYPE_DETAILED_POLL_RESPONSE)

                def startCal = Calendar.getInstance()
                startCal.time = pollResponse.startTime

                def endCal = Calendar.getInstance()
                endCal.time = pollResponse.endTime

                pollResponseNode.set('startTime', startCal)
                pollResponseNode.set('endTime', endCal)
                pollResponseNode.set('responseType', pollResponse.responseType as String)
                pollResponseNode.set('stackTrace', pollResponse.stackTrace)
                pollResponseNode.set('cleared', pollResponse.cleared)
            }

            session.save()

        } catch (Exception e) {

            log.error("An error occurred while attempting to persist the record holder for service ${recordHolder.canonicalMonitorName}", e)

        } finally {

            session?.logout()
        }
    }

    @Override
    Optional<RecordHolder> getRecordHolder(String monitorClass) {

        Optional.absent()
    }
}
