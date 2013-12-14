package com.citytechinc.monitoring.services.manager.actors

import groovyx.gpars.actor.DynamicDispatchActor

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class RecordPersistenceServiceActor extends DynamicDispatchActor {

    static class GetRecords {}
    static class ResumePolling {}
}
