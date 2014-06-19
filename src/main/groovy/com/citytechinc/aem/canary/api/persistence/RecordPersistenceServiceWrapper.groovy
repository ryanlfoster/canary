package com.citytechinc.aem.canary.api.persistence

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

    @Delegate
    final RecordPersistenceService service

    final String identifier
    final Integer ranking
    final Boolean providesReadOperations
    final Boolean providesWriteOperations

    public RecordPersistenceServiceWrapper(final RecordPersistenceService service) {
        this.service = service

        def definition = service.getClass().getAnnotation(RecordPersistenceServiceDefinition)

        identifier = service.class.canonicalName
        ranking = definition.ranking()
        providesReadOperations = definition.providesReadOperations()
        providesWriteOperations = definition.providesWriteOperations()
    }
}
