package com.citytechinc.monitoring.jmx;

import com.adobe.granite.jmx.annotation.Description;

/**
 *
 * @author CITYTECH, INC. 2013
 *
 */
@Description("CITYTECH OSGi Service Monitor Management")
public interface ServiceMonitorManagerMBean {

    /**
     *
     */
    @Description("Instruct the ServiceMonitorManager to poll all monitors")
    public void forcePoll();

}
