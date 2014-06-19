package com.citytechinc.aem.canary

import com.citytechinc.cq.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
class Activator implements BundleActivator{

    @Override
    void start(BundleContext bundleContext) throws Exception {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()
    }

    @Override
    void stop(BundleContext bundleContext) throws Exception {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()
    }
}

