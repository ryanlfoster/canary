package com.citytechinc.canary.api.monitor

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LogEscalatingMonitor {

    /**
     *
     * @return
     */
    String logFilePrefix()

}