package com.citytechinc.canary.jmx;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.citytechinc.canary.Constants;
import com.citytechinc.canary.api.monitor.DetailedPollResponse;
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper;
import com.citytechinc.canary.api.monitor.PollResponseType;
import com.citytechinc.canary.api.monitor.RecordHolder;
import com.citytechinc.canary.services.manager.ServiceManager;
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
import java.util.Date;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Property(name = "jmx.objectname", value = "com.citytechinc.canary.jmx:type=CITYTECH Canary Framework Maintenance")
@Service
public final class CanaryMaintenanceMBeanImpl extends AnnotatedStandardMBean implements CanaryMaintenanceMBean {

    private static final Logger LOG = LoggerFactory.getLogger(CanaryMaintenanceMBeanImpl.class);

    @Reference
    ServiceManager serviceManager;

    public CanaryMaintenanceMBeanImpl() throws NotCompliantMBeanException {
        super(CanaryMaintenanceMBean.class);
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
    public TabularDataSupport getMonitors() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitor Names", "Monitor Names", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final MonitoredServiceWrapper wrapper : serviceManager.getMonitoredServicesConfigurations()) {

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] { wrapper.getIdentifier() } ));
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the monitors", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getRecordsForMonitor(String monitoredService) {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Start Time",
                    "End Time",
                    "Runtime (ms)",
                    "Response",
                    "Message",
                    "Cleared"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.LONG,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.BOOLEAN};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("Records for Monitored Service", "asdf", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            Optional<RecordHolder> record = serviceManager.getRecordHolder(monitoredService);

            if (record.isPresent()) {

                for (final DetailedPollResponse detailedPollResponse : record.get().getRecords()) {

                    tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                            Constants.JMX_DATE_TIME_FORMATTER.format(detailedPollResponse.getStartTime()),
                            Constants.JMX_DATE_TIME_FORMATTER.format(detailedPollResponse.getStartTime()),
                            detailedPollResponse.executionTimeInMilliseconds(),
                            detailedPollResponse.getResponseType().toString(),
                            detailedPollResponse.getMessages(),
                            detailedPollResponse.getExcused() }));
                }
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the monitor poll response records", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public String getStacktraceForMonitor(String monitoredService, String startDate) {

        String stacktrace = "n/a";

        Optional<RecordHolder> record = serviceManager.getRecordHolder(monitoredService);

        if (record.isPresent()) {

            for (final DetailedPollResponse detailedPollResponse : record.get().getRecords()) {

                final Date dateToCompare = Constants.JMX_DATE_TIME_PARSER.parseDateTime(startDate).toDate();

                if (detailedPollResponse.getStartTime() == dateToCompare && detailedPollResponse.getResponseType() == PollResponseType.EXCEPTION) {
                    stacktrace = detailedPollResponse.getStackTrace();
                }
            }
        }

        return stacktrace;
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