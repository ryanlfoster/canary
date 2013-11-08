package com.citytechinc.monitoring.services.persistence;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public final class RecordPersistenceServiceWrapper {

    private final RecordPersistenceService service;
    private final RecordPersistenceServiceDefinition definition;

    public RecordPersistenceServiceWrapper(final RecordPersistenceService service) {
        this.service = service;
        this.definition = service.getClass().getAnnotation(RecordPersistenceServiceDefinition.class);
    }

    public RecordPersistenceServiceDefinition getDefinition() {
        return definition;
    }

    public RecordPersistenceService getService() {
        return service;
    }
}
