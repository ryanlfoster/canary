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

/**
 *
 * @author CITYTECH, INC. 2013
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
    public void forcePoll() {
        serviceManager.forcePoll();
    }

}