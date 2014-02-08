package com.citytechinc.canary.api.persistence

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

    @Delegate final RecordPersistenceService service
    final String identifier
    final RecordPersistenceServiceDefinition definition

    public RecordPersistenceServiceWrapper(final RecordPersistenceService service) {
        this.service = service
        identifier = service.class.canonicalName
        this.definition = service.getClass().getAnnotation(RecordPersistenceServiceDefinition.class)
    }
}
