package com.citytechinc.monitoring.api.persistence

import groovy.transform.EqualsAndHashCode

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@EqualsAndHashCode
public final class RecordPersistenceServiceWrapper {

    final RecordPersistenceService service
    final RecordPersistenceServiceDefinition definition

    public RecordPersistenceServiceWrapper(final RecordPersistenceService service) {
        this.service = service
        this.definition = service.getClass().getAnnotation(RecordPersistenceServiceDefinition.class)
    }
}
