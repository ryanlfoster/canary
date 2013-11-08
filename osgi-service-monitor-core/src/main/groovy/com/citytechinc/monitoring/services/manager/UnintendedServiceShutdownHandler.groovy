//package com.citytechinc.monitoring.services.manager
//
//import com.citytechinc.monitoring.services.monitor.state.ServiceStateMonitor
//import com.google.common.collect.Lists
//import groovy.util.logging.Slf4j
//import org.apache.felix.scr.ScrService
//import org.apache.felix.scr.annotations.Component
//import org.apache.felix.scr.annotations.Property
//import org.apache.felix.scr.annotations.Properties
//import org.apache.felix.scr.annotations.Reference
//import org.apache.felix.scr.annotations.ReferenceCardinality
//import org.apache.felix.scr.annotations.ReferencePolicy
//import org.apache.felix.scr.annotations.Service
//import org.osgi.service.event.Event
//import org.osgi.service.event.EventConstants
//import org.osgi.service.event.EventHandler
//
///**
// *
// * @author CITYTECH, INC. 2013
// *
// */
//@Component(immediate = true)
//@Service(value = EventHandler)
//@Properties(value = [
//    @Property(name = EventConstants.EVENT_TOPIC, value = 'org/osgi/framework/ServiceEvent')])
//@Slf4j
//class UnintendedServiceShutdownHandler implements EventHandler {
//
//    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC, referenceInterface = ServiceStateMonitor, bind = "bindStateMonitors", unbind = "unbindStateMonitors")
//    List<String> stateMonitors = Lists.newCopyOnWriteArrayList()
//
//    @Reference
//    ScrService scrService
//
//    @Reference
//    ServiceManager serviceManager
//
//    protected void bindStateMonitors(final ServiceStateMonitor serviceStateMonitor) {
//        stateMonitors.addAll(serviceStateMonitor.serviceName)
//    }
//
//    protected void unbindStateMonitors(final ServiceStateMonitor serviceStateMonitor) {
//        stateMonitors.remove(serviceStateMonitor)
//    }
//
//    @Override
//    void handleEvent(Event event) {
//
//        log.info("Processing event ${event}")
//    }
//}
