package com.citytechinc.monitoring.services.logging

import com.citytechinc.monitoring.constants.Constants
import com.google.common.base.Optional
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Service
import org.osgi.framework.Constants as OsgiConstants

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(label = 'CITYTECH Service Monitor Log Management Service', description = 'Is responsible for handling escalation of logs for alarms, reading log output, and removing alarms.', immediate = true)
@Service
@Properties(value = [
    @Property(name = OsgiConstants.SERVICE_VENDOR, value = Constants.CITYTECH_SERVICE_VENDOR_NAME) ])
@Slf4j
class DefaultLogManagementService implements LogManagementService {

    @Override
    void removeDebugLogging(String classname) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void addDebugLogging(String classname) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Optional<String> readDebugLog(String classname) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}
