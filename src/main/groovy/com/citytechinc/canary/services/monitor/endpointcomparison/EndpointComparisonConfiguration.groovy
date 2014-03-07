package com.citytechinc.canary.services.monitor.endpointcomparison

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2014
 *
 * Copyright 2014 CITYTECH, Inc.
 *
 */
interface EndpointComparisonConfiguration {

    enum TargetType { LOAD_BALANCER, APPSERVER, WEBSERVER }

    /**
     *
     * @return
     */
    String getLabel()

    /**
     *
     * @return
     */
    TargetType getType()

    /**
     *
     * @return
     */
    String getURL()
}
