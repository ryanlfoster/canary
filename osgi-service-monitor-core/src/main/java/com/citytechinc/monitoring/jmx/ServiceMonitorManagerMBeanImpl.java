package com.citytechinc.monitoring.jmx;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;
import com.citytechinc.monitoring.services.manager.ServiceManager;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.TabularDataSupport;
import java.util.List;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Property(name = "jmx.objectname", value = "com.citytechinc.monitoring.jmx:type=CITYTECH OSGi Service Monitor Management")
@Service
public final class ServiceMonitorManagerMBeanImpl extends AnnotatedStandardMBean implements ServiceMonitorManagerMBean {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceMonitorManagerMBeanImpl.class);

    @Reference
    ServiceManager serviceManager;

    public ServiceMonitorManagerMBeanImpl() throws NotCompliantMBeanException {
        super(ServiceMonitorManagerMBean.class);
    }

    @Override
    public void requestAllMonitorsPoll() {
        serviceManager.requestAllMonitorsPoll();
    }

    @Override
    public void requestAllMonitorsPersist() {
        serviceManager.requestAllMonitorsPersist();
    }

    @Override
    public TabularDataSupport listMonitoredServices() {
        return null;
    }

    @Override
    public TabularDataSupport listRecordPersistenceServices() {
        return null;
    }

    @Override
    public TabularDataSupport listNotificationAgents() {
        return null;
    }

    @Override
    public TabularDataSupport listPollResponseHandlers() {
        return null;
    }

    @Override
    public List<String> listAlarmedMonitors() {
        return null;
    }

    @Override
    public void resetAllAlarms() {
        serviceManager.resetAllAlarms();
    }

    @Override
    public void resetAlarm(final String fullyQualifiedMonitorPath) {
        serviceManager.resetAlarm(fullyQualifiedMonitorPath);
    }

}