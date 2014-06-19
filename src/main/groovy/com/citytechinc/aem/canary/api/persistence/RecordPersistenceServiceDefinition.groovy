package com.citytechinc.aem.canary.api.persistence

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordPersistenceServiceDefinition {

    /**
     *
     * The service ranking is used only at Monitored Service Actor instantiation. The highest ranked Record Persistence
     *   Service which provides read operations is selected to seed the Record Holder for a newly instantiated monitor.
     *
     * @return
     */
    int ranking() default 100

    /**
     *
     * Indicates that the record persistence service should be used for read operations.
     *
     * @return
     */
    boolean providesReadOperations() default true

    /**
     *
     * Indicates that the record persistence service should be used for write operations.
     *
     * @return
     */
    boolean providesWriteOperations() default true

}