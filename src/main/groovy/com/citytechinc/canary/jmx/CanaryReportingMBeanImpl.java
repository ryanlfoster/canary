package com.citytechinc.canary.jmx;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper;
import com.citytechinc.canary.api.notification.NotificationAgentWrapper;
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper;
import com.citytechinc.canary.api.responsehandler.PollResponseHandlerWrapper;
import com.citytechinc.canary.Constants;
import com.citytechinc.canary.services.manager.ServiceManager;
import com.citytechinc.canary.services.manager.actors.MissionControlActor;
import com.citytechinc.canary.services.manager.actors.Statistics;
import com.citytechinc.canary.api.monitor.RecordHolder;
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

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Property(name = "jmx.objectname", value = "com.citytechinc.canary.jmx:type=CITYTECH Canary Framework Reporting")
@Service
public final class CanaryReportingMBeanImpl extends AnnotatedStandardMBean implements CanaryReportingMBean {

    private static final Logger LOG = LoggerFactory.getLogger(CanaryReportingMBeanImpl.class);

    @Reference
    ServiceManager serviceManager;

    public CanaryReportingMBeanImpl() throws NotCompliantMBeanException {
        super(CanaryReportingMBean.class);
    }

    @Override
    public TabularDataSupport getMonitoredServicesConfigurations() {


        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Classname",
                    "Name",
                    "Description",
                    "Poll Interval",
                    "Alarm Criteria",
                    "Alarm Threshold",
                    "History Size",
                    "Persist when alarmed?",
                    "Max Execution Time (ms)",
                    "Auto Reset?",
                    "Log Escalation?"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,      // Classname
                    SimpleType.STRING,      // Name
                    SimpleType.STRING,      // Description
                    SimpleType.STRING,      // Configured Poll Interval
                    SimpleType.STRING,      // Alarm Criteria
                    SimpleType.INTEGER,     // Alarm Threshold
                    SimpleType.INTEGER,     // Configured History Size
                    SimpleType.BOOLEAN,     // Configured to persist when alarmed?
                    SimpleType.LONG,        // Configured Max Execution Time
                    SimpleType.STRING,      // Configured to Auto Reset?
                    SimpleType.BOOLEAN};    // Log escalation?

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitor Service Configurations", "Monitor Configurations", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final MonitoredServiceWrapper wrapper : serviceManager.getMonitoredServicesConfigurations()) {

                final String autoResume;

                if (wrapper.getResetCriteriaDefined()) {
                    autoResume = "Polls every " + wrapper.getResetInterval() + " " + wrapper.getResetIntervalUnit() + " and resets after ";
                } else {
                    autoResume = "N/A";
                }

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getIdentifier(),
                        wrapper.getName(),
                        wrapper.getDescription(),
                        wrapper.getPollInterval() + " " + wrapper.getPollIntervalUnit(),
                        wrapper.getAlarmCriteria().toString(),
                        wrapper.getAlarmThreshold(),
                        wrapper.getMaxNumberOfRecords(),
                        wrapper.getPersistWhenAlarmed(),
                        wrapper.getMaxExecutionTime(),
                        autoResume,
                        wrapper.getEscalateLogs()}));
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor Service Configurations", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getNotificationAgentsConfigurations() {

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
            final TabularType pageTabularType = new TabularType("List of Notification Agents Configurations", "Notification Agent Configurations", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final NotificationAgentWrapper wrapper : serviceManager.getNotificationAgentsConfigurations()) {

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getIdentifier(),
                        wrapper.getStrategy().toString(),
                        wrapper.getSpecifics().toString() }));
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Notification Agents Configurations", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getPollResponseHandlersConfigurations() {

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
            final TabularType pageTabularType = new TabularType("List of Poll Response Handler Configurations", "Poll Response Handler Configurations", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final PollResponseHandlerWrapper wrapper : serviceManager.getPollResponseHandlersConfigurations()) {

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getIdentifier(),
                        wrapper.getStrategy().toString(),
                        wrapper.getSpecifics().toString()}));
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Poll Response Handlers Configurations", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getRecordPersistenceServicesConfigurations() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Ranking",
                    "Provides Read Operations",
                    "Provides Write Operations"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.BOOLEAN,
                    SimpleType.BOOLEAN};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Record Persistence Service Configurations", "Record Persistence Service Configurations", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final RecordPersistenceServiceWrapper wrapper : serviceManager.getRecordPersistenceServicesConfigurations()) {

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getIdentifier(),
                        wrapper.getRanking(),
                        wrapper.getProvidesReadOperations(),
                        wrapper.getProvidesWriteOperations()}));
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Record Persistence Services Configurations", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getMonitoredServicesPollResults() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Alarmed?",
                    "Recent Poll Date",
                    "Recent Poll Response",
                    "Avg Exec Time (ms)",
                    "Failure Rate",
                    "Total Polls",
                    "Total Failures"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.BOOLEAN,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.LONG,
                    SimpleType.BIGDECIMAL,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Monitor Results", "Monitor Results", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final MonitoredServiceWrapper wrapper : serviceManager.getMonitoredServicesConfigurations()) {

                final Optional<RecordHolder> optionalRecord = serviceManager.getRecordHolder(wrapper.getIdentifier());

                if (optionalRecord.isPresent()) {

                    final RecordHolder record = optionalRecord.get();

                    tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                            wrapper.getIdentifier(),
                            record.isAlarmed(),
                            record.mostRecentPollDate().isPresent() ? Constants.JMX_DATE_TIME_FORMATTER.format(record.mostRecentPollDate().get()) : "--",
                            record.mostRecentPollResponse().isPresent() ? record.mostRecentPollResponse().get().toString() : "--",
                            record.averagePollExecutionTime(false),
                            record.failureRate(false),
                            record.numberOfPolls(),
                            record.numberOfFailures()}));
                }
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor Results", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getNotificationAgentsStatistics() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Number of Delivered Messages",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Avg Message Process Time (ms)"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.LONG};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Notification Agent Statistics", "Notification Agent Statistics", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final NotificationAgentWrapper wrapper : serviceManager.getNotificationAgentsConfigurations()) {

                final Statistics statistics = serviceManager.getStatistics(wrapper.getIdentifier(), MissionControlActor.GetStatistics.Type.NOTIFICATION_AGENT).get();

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getAgent().getClass().getCanonicalName(),
                        statistics.getDeliveredMessages(),
                        statistics.getProcessedMessages(),
                        statistics.getMessageExceptions(),
                        statistics.getAverageMessageProcessTime()}));
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Notification Agent statistics", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getPollResponseHandlersStatistics() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Number of Delivered Messages",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Avg Message Process Time (ms)"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.LONG};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Poll Response Handler Statistics", "Poll Response Handler Statistics", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final PollResponseHandlerWrapper wrapper : serviceManager.getPollResponseHandlersConfigurations()) {

                final Statistics statistics = serviceManager.getStatistics(wrapper.getIdentifier(), MissionControlActor.GetStatistics.Type.POLL_RESPONSE_HANDLER).get();

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getHandler().getClass().getCanonicalName(),
                        statistics.getDeliveredMessages(),
                        statistics.getProcessedMessages(),
                        statistics.getMessageExceptions(),
                        statistics.getAverageMessageProcessTime()}));
            }

        } catch (final Exception exception) {

            LOG.error("An exception has occurred building the TabularDataSupport listing the Poll Response Handler statistics", exception);
        }

        return tabularDataSupport;
    }

    @Override
    public TabularDataSupport getRecordPersistenceServicesStatistics() {

        TabularDataSupport tabularDataSupport = null;

        try {

            final String[] itemNamesDescriptionsAndIndexName = {
                    "Name",
                    "Number of Delivered Messages",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Avg Message Process Time (ms)"};

            final OpenType[] itemTypes = {
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.LONG};

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes);
            final TabularType pageTabularType = new TabularType("List of Record Persistence Services Statistics", "Record Persistence Services Statistics", pageType, itemNamesDescriptionsAndIndexName);
            tabularDataSupport = new TabularDataSupport(pageTabularType);

            for (final RecordPersistenceServiceWrapper wrapper : serviceManager.getRecordPersistenceServicesConfigurations()) {

                final Statistics statistics = serviceManager.getStatistics(wrapper.getIdentifier(), MissionControlActor.GetStatistics.Type.RECORD_PERSISTENCE_SERVICE).get();

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, new Object[] {
                        wrapper.getIdentifier(),
                        statistics.getDeliveredMessages(),
                        statistics.getProcessedMessages(),
                        statistics.getMessageExceptions(),
                        statistics.getAverageMessageProcessTime()}));
            }

        } catch (final Exception exception) {

            LOG.error("An exception has occurred building the TabularDataSupport while listing the Record Persistence Services statistics", exception);
        }

        return tabularDataSupport;
    }
}