package com.citytechinc.monitoring.jmx;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;
import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper;
import com.citytechinc.monitoring.api.responsehandler.PollResponseWrapper;
import com.citytechinc.monitoring.constants.Constants;
import com.citytechinc.monitoring.services.jcrpersistence.DetailedPollResponse;
import com.citytechinc.monitoring.services.jcrpersistence.RecordHolder;
import com.citytechinc.monitoring.services.manager.ServiceManager;
import com.citytechinc.monitoring.services.manager.actors.monitor.Statistics;
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
    public TabularDataSupport getMonitorDefinitions() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Poll Interval",
                    "Alarm Threshold",
                    "History Size",
                    "Persist when alarmed?",
                    "Max Exec Time",
                    "Auto Resume?"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.BOOLEAN,
                    SimpleType.INTEGER,
                    SimpleType.STRING};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitors", "Monitor Definitions", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final MonitoredServiceWrapper wrapper : serviceManager.listMonitoredServices()) {

                final String autoResume;

                if (wrapper.getAutoResumingPollerDefinition() != null) {

                    autoResume = wrapper.getAutoResumingPollerDefinition().interval() + " " + wrapper.getAutoResumingPollerDefinition().unit();

                } else {

                    autoResume = "--";
                }

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                    wrapper.getMonitor().getClass().getCanonicalName(),
                    wrapper.getDefinition().pollInterval() + " " + wrapper.getDefinition().pollIntervalUnit(),
                    wrapper.getDefinition().alarmThreshold(),
                    wrapper.getDefinition().maxPollHistoryEntries(),
                    wrapper.getDefinition().persistWhenAlarmed(),
                    wrapper.getDefinition().pollMaxExecutionTimeInSeconds(),
                    autoResume}));
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor Definitions", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getMonitorStates() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Alarmed?",
                    "1st Poll Date",
                    "Recent Poll Date",
                    "Recent Poll Response",
                    "Avg Exec Time (ms)",
                    "Record Polls",
                    "Record Failures",
                    "Lifetime Polls",
                    "Lifetime Failures"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.BOOLEAN,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.LONG,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitors", "Monitor States", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final MonitoredServiceWrapper wrapper : serviceManager.listMonitoredServices()) {

                final RecordHolder record = serviceManager.getRecordHolder(wrapper.getCanonicalMonitorName());

                if (record != null) {

                    tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                            wrapper.getCanonicalMonitorName(),
                            record.isAlarmed(),
                            record.firstPoll().isPresent() ? Constants.JMX_DATE_TIME_FORMATTER.format(record.firstPoll().get()) : "--",
                            record.mostRecentPollDate().isPresent() ? Constants.JMX_DATE_TIME_FORMATTER.format(record.mostRecentPollDate().get()) : "--",
                            record.mostRecentPollResponse().isPresent() ? record.mostRecentPollResponse().get().toString() : "--",
                            record.averagePollExecutionTime(),
                            record.recordNumberOfPolls(),
                            record.recordNumberOfFailures(),
                            record.getLifetimeNumberOfPolls(),
                            record.getLifetimeNumberOfFailures() }));
                } else {

                    LOG.warn("Record for {} is non-existent", wrapper.getCanonicalMonitorName());
                }
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor States", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getPollResponseHandlerDefinitions() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Strategy",
                    "Specifics"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitors", "Poll Response Handler Definitions", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);


            for (final PollResponseWrapper wrapper : serviceManager.listPollResponseHandlers()) {

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getHandler().getClass().getCanonicalName(),
                        wrapper.getDefinition().strategy().toString(),
                        wrapper.getDefinition().specifics().toString()}));

            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Poll Response Handler Definitions", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getPollResponseHandlerStatistics() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Average Message Process Time"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.LONG};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitors", "Poll Response Handler Statistics", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final PollResponseWrapper wrapper : serviceManager.listPollResponseHandlers()) {

                final Statistics statistics = serviceManager.getPollResponseHandlerStatistics(wrapper.getHandler().getClass().getCanonicalName());

                if (statistics != null) {

                    tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                            wrapper.getHandler().getClass().getCanonicalName(),
                            statistics.getNumberOfProcessedMessages(),
                            statistics.getNumberOfMessageExceptions(),
                            statistics.getAverageMessageProcessTime()}));
                } else {

                    LOG.warn("Statistics for {} is non-existent", wrapper.getHandler().getClass().getCanonicalName());
                }
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor States", exception);
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
                    "Stacktrace",
                    "Cleared"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.LONG,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.BOOLEAN};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitors", "In-memory poll responses for a Monitor", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            final RecordHolder record = serviceManager.getRecordHolder(monitoredService);

            if (record != null) {

                for (final DetailedPollResponse detailedPollResponse : record.getRecords()) {

                    tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                            Constants.JMX_DATE_TIME_FORMATTER.format(detailedPollResponse.getStartTime()),
                            Constants.JMX_DATE_TIME_FORMATTER.format(detailedPollResponse.getStartTime()),
                            detailedPollResponse.runTimeInMilliseconds(),
                            detailedPollResponse.getResponseType().toString(),
                            detailedPollResponse.getStackTrace(),
                            detailedPollResponse.getCleared() }));
                }

            } else {

                LOG.warn("Record for {} is non-existent", monitoredService);
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor States", exception);
        }

        return tabularDataSupport;
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