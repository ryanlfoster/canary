package com.citytechinc.monitoring.jmx;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.citytechinc.monitoring.services.manager.ServiceManager;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.NotCompliantMBeanException;
import java.util.List;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Property(name = "jmx.objectname", value = "com.citytechinc.monitoring.services:type=CITYTECH OSGi Service Monitor Management")
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
    public List<String> listMonitoredServices() {
        return serviceManager.listMonitoredServices();
    }

    @Override
    public List<String> listRecordPersistenceServices() {
        return serviceManager.listRecordPersistenceServices();
    }

    @Override
    public List<String> listNotificationAgents() {
        return serviceManager.listNotificationAgents();
    }

    @Override
    public List<String> listPollResponseHandlers() {
        return serviceManager.listPollResponseHandlers();
    }

    @Override
    public List<String> listAlarmedMonitors() {
        return serviceManager.listAlarmedMonitors();
    }
}