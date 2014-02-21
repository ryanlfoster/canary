package com.citytechinc.canary.jmx;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.citytechinc.canary.Constants;
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper;
import com.citytechinc.canary.api.monitor.RecordHolder;
import com.citytechinc.canary.api.notification.NotificationAgentWrapper;
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper;
import com.citytechinc.canary.api.responsehandler.PollResponseHandlerWrapper;
import com.citytechinc.canary.services.manager.ServiceManager;
import com.citytechinc.canary.services.manager.actors.MissionControlActor;
import com.citytechinc.canary.services.manager.actors.Statistics;
import com.citytechinc.canary.services.statemonitoring.StateMonitorWatchdog;
import com.google.common.base.Optional;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import java.util.Arrays;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Property(name = "jmx.objectname", value = "com.citytechinc.canary.jmx:type=CITYTECH Canary Framework Configured State Monitoring")
@Service
public final class CanaryConfiguredStateMonitoringMBeanImpl extends AnnotatedStandardMBean implements CanaryConfiguredStateMonitoringMBean {

    private static final Logger LOG = LoggerFactory.getLogger(CanaryConfiguredStateMonitoringMBeanImpl.class);

    @Reference
    StateMonitorWatchdog stateMonitorWatchdog;

    public CanaryConfiguredStateMonitoringMBeanImpl() throws NotCompliantMBeanException {
        super(CanaryConfiguredStateMonitoringMBean.class);
    }


    @Override
    public TabularDataSupport getMonitoredComponentStateConfigurations() {
        return null;
    }

    @Override
    public TabularDataSupport getMonitoredBundleStateConfigurations() {
        return null;
    }
}