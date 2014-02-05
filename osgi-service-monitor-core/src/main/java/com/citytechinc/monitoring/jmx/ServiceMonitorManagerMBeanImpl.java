package com.citytechinc.monitoring.jmx;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.citytechinc.monitoring.api.monitor.MonitoredServiceWrapper;
import com.citytechinc.monitoring.api.notification.NotificationAgentWrapper;
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
import java.util.Arrays;

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Property(name = "jmx.objectname", value = "com.citytechinc.monitoring.jmx:type=CITYTECH OSGi Service Monitor Management and Reporting")
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
    public TabularDataSupport getMonitors() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Configured Poll Interval",
                    "Configured Alarm Threshold",
                    "Configured History Size",
                    "Configured to persist when alarmed?",
                    "Configured Max Execution Time",
                    "Configured to Auto Resume?",
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
                    SimpleType.STRING,      // Name
                    SimpleType.STRING,      // Configured Poll Interval
                    SimpleType.INTEGER,     // Configured Alarm Threshold
                    SimpleType.INTEGER,     // Configured History Size
                    SimpleType.BOOLEAN,     // Configured to persist when alarmed?
                    SimpleType.INTEGER,     // Configured Max Execution Time
                    SimpleType.STRING,      // Configured to Auto Resume?

                    SimpleType.STRING,     // Alarmed? (was bool)
                    SimpleType.STRING,      // 1st Poll Date
                    SimpleType.STRING,      // Recent Poll Date
                    SimpleType.STRING,      // Recent Poll Response
                    SimpleType.STRING,        // Avg Exec Time (ms) (was long)
                    SimpleType.STRING,     // Record Polls (was int)
                    SimpleType.STRING,     // Record Failures (was int)
                    SimpleType.STRING,     // Lifetime Polls (was int)
                    SimpleType.STRING};    // Lifetime Failures (was int)

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitors", "Monitor States", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final MonitoredServiceWrapper wrapper : serviceManager.listMonitoredServices()) {

                final String autoResume;

                if (wrapper.getAutoResumingPollerDefinition() != null) {

                    autoResume = wrapper.getAutoResumingPollerDefinition().interval() + " " + wrapper.getAutoResumingPollerDefinition().unit();

                } else {

                    autoResume = "--";
                }

                final RecordHolder record = serviceManager.getRecordHolder(wrapper.getCanonicalMonitorName());

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getMonitor().getClass().getCanonicalName(),
                        wrapper.getDefinition().pollInterval() + " " + wrapper.getDefinition().pollIntervalUnit(),
                        wrapper.getDefinition().alarmThreshold(),
                        wrapper.getDefinition().maxPollHistoryEntries(),
                        wrapper.getDefinition().persistWhenAlarmed(),
                        wrapper.getDefinition().pollMaxExecutionTimeInSeconds(),
                        autoResume,
                        record != null ? record.isAlarmed().toString() : "--",
                        record != null && record.firstPoll().isPresent() ? Constants.JMX_DATE_TIME_FORMATTER.format(record.firstPoll().get()) : "--",
                        record != null && record.mostRecentPollDate().isPresent() ? Constants.JMX_DATE_TIME_FORMATTER.format(record.mostRecentPollDate().get()) : "--",
                        record != null && record.mostRecentPollResponse().isPresent() ? record.mostRecentPollResponse().get().toString() : "--",
                        record != null ? record.averagePollExecutionTime().toString() : "--",
                        record != null ? record.recordNumberOfPolls().toString() : "--",
                        record != null ? record.recordNumberOfFailures().toString() : "--",
                        record != null ? record.getLifetimeNumberOfPolls().toString() : "--",
                        record != null ? record.getLifetimeNumberOfFailures().toString() : "--"}));
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor States", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getNotificationAgents() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Strategy",
                    "Specifics",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Avg Message Process Time (ms)"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Notification Agents", "Poll Response Handler Definitions", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final NotificationAgentWrapper wrapper : serviceManager.listNotificationAgents()) {

                final Statistics statistics = serviceManager.getNotificationAgentStatistics(wrapper.getAgent().getClass().getCanonicalName());

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getAgent().getClass().getCanonicalName(),
                        wrapper.getDefinition().strategy().toString(),
                        Arrays.asList(wrapper.getDefinition().specifics()).toString(),
                        statistics != null ? statistics.getNumberOfProcessedMessages().toString() : "--",
                        statistics != null ? statistics.getNumberOfMessageExceptions().toString() : "--",
                        statistics != null ? statistics.getAverageMessageProcessTime().toString() : "--"}));

            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Poll Response Handler Definitions", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getPollResponseHandlers() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Strategy",
                    "Specifics",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Avg Message Process Time (ms)"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Poll Response Handlers", "Poll Response Handler Definitions", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);


            for (final PollResponseWrapper wrapper : serviceManager.listPollResponseHandlers()) {

                final Statistics statistics = serviceManager.getPollResponseHandlerStatistics(wrapper.getHandler().getClass().getCanonicalName());

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getHandler().getClass().getCanonicalName(),
                        wrapper.getDefinition().strategy().toString(),
                        Arrays.asList(wrapper.getDefinition().specifics()).toString(),
                        statistics != null ? statistics.getNumberOfProcessedMessages().toString() : "--",
                        statistics != null ? statistics.getNumberOfMessageExceptions().toString() : "--",
                        statistics != null ? statistics.getAverageMessageProcessTime().toString() : "--"}));

            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Poll Response Handler Definitions", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getRecordPersistenceServices() {
        return null;
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