package com.citytechinc.monitoring.services.logging

import com.google.common.base.Optional

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
public interface LogManagementService {

    /**
     *
     * @param classname
     */
    void removeDebugLogging(String classname)

    /**
     *
     *
     *
     * @param classname
     */
    void addDebugLogging(String classname)

    /**
     *
     * Returns an optional wrapper of type String. If present, the string represents the log file.
     *
     * @param classname
     */
    Optional<String> readDebugLog(String classname)
}