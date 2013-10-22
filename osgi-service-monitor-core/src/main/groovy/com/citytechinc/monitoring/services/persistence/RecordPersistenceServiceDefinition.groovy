package com.citytechinc.monitoring.services.persistence

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public @interface RecordPersistenceServiceDefinition {

    /**
     *
     * The top ranking service is used to load records on start.
     *
     * @return
     */
    int ranking() default 100
}