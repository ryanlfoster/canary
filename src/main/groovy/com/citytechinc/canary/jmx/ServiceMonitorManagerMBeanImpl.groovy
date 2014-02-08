package com.citytechinc.canary.jmx

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean
import com.citytechinc.canary.api.monitor.MonitoredServiceWrapper
import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.persistence.RecordPersistenceServiceWrapper
import com.citytechinc.canary.api.responsehandler.PollResponseWrapper
import com.citytechinc.canary.constants.Constants
import com.citytechinc.canary.services.persistence.DetailedPollResponse
import com.citytechinc.canary.services.persistence.RecordHolder
import com.citytechinc.canary.services.manager.ServiceManager
import com.citytechinc.canary.services.manager.actors.MissionControlActor
import com.citytechinc.canary.services.manager.actors.Statistics
import com.google.common.base.Optional
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.management.NotCompliantMBeanException
import javax.management.openmbean.CompositeDataSupport
import javax.management.openmbean.CompositeType
import javax.management.openmbean.OpenType
import javax.management.openmbean.SimpleType
import javax.management.openmbean.TabularDataSupport
import javax.management.openmbean.TabularType

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Component(immediate = true)
@Property(name = "jmx.objectname", value = "com.citytechinc.canary.jmx:type=CITYTECH, Inc. CQ Canary Framework Management and Reporting")
@Service
public final class ServiceMonitorManagerMBeanImpl extends AnnotatedStandardMBean implements ServiceMonitorManagerMBean {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceMonitorManagerMBeanImpl.class)

    @Reference
    ServiceManager serviceManager

    public ServiceMonitorManagerMBeanImpl() throws NotCompliantMBeanException {
        super(ServiceMonitorManagerMBean)
    }

    @Override
    public void requestAllMonitorsPoll() {
        serviceManager.requestAllMonitorsPoll()
    }

    @Override
    public void requestAllMonitorsPersist() {
        serviceManager.requestAllMonitorsPersist()
    }

    @Override
    public TabularDataSupport getMonitors() {

        TabularDataSupport tabularDataSupport = null

        try {

            final String[] itemNamesDescriptionsAndIndexName = [
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
                    "Lifetime Failures"]

            final OpenType[] itemTypes = [
                    SimpleType.STRING,      // Name
                    SimpleType.STRING,      // Configured Poll Interval
                    SimpleType.INTEGER,     // Configured Alarm Threshold
                    SimpleType.INTEGER,     // Configured History Size
                    SimpleType.BOOLEAN,     // Configured to persist when alarmed?
                    SimpleType.INTEGER,     // Configured Max Execution Time
                    SimpleType.STRING,      // Configured to Auto Resume?

                    SimpleType.BOOLEAN,     // Alarmed?
                    SimpleType.STRING,      // 1st Poll Date
                    SimpleType.STRING,      // Recent Poll Date
                    SimpleType.STRING,      // Recent Poll Response
                    SimpleType.LONG,        // Avg Exec Time (ms)
                    SimpleType.INTEGER,     // Record Polls
                    SimpleType.INTEGER,     // Record Failures
                    SimpleType.INTEGER,     // Lifetime Polls
                    SimpleType.INTEGER]    // Lifetime Failures

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes)
            final TabularType pageTabularType = new TabularType("List of Monitors", "Monitor States", pageType, itemNamesDescriptionsAndIndexName)
            tabularDataSupport = new TabularDataSupport(pageTabularType)

            for (final MonitoredServiceWrapper wrapper : serviceManager.getMonitoredServices()) {

                final String autoResume

                if (wrapper.getAutoResumingPollerDefinition() != null) {

                    autoResume = wrapper.getAutoResumingPollerDefinition().interval() + " " + wrapper.getAutoResumingPollerDefinition().unit()

                } else {

                    autoResume = "--"
                }

                final RecordHolder record = serviceManager.getRecordHolder(wrapper.getCanonicalMonitorName()).get()

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, [
                        wrapper.getMonitor().getClass().getCanonicalName(),
                        wrapper.getDefinition().pollInterval() + " " + wrapper.getDefinition().pollIntervalUnit(),
                        wrapper.getDefinition().alarmThreshold(),
                        wrapper.getDefinition().maxPollHistoryEntries(),
                        wrapper.getDefinition().persistWhenAlarmed(),
                        wrapper.getDefinition().pollMaxExecutionTimeInSeconds(),
                        autoResume,
                        record.isAlarmed(),
                        record.firstPoll().isPresent() ? Constants.JMX_DATE_TIME_FORMATTER.format(record.firstPoll().get()) : "--",
                        record.mostRecentPollDate().isPresent() ? Constants.JMX_DATE_TIME_FORMATTER.format(record.mostRecentPollDate().get()) : "--",
                        record.mostRecentPollResponse().isPresent() ? record.mostRecentPollResponse().get().toString() : "--",
                        record.averagePollExecutionTime(),
                        record.recordNumberOfPolls(),
                        record.recordNumberOfFailures(),
                        record.getLifetimeNumberOfPolls(),
                        record.getLifetimeNumberOfFailures()]))
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor States", exception)
        }

        return tabularDataSupport
    }

    @Override
    public TabularDataSupport getNotificationAgents() {

        TabularDataSupport tabularDataSupport = null

        try {

            def itemNamesDescriptionsAndIndexName = [
                    "Name",
                    "Strategy",
                    "Specifics",
                    "Number of Delivered Messages",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Avg Message Process Time (ms)"]

            final OpenType[] itemTypes = [
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.LONG]

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes)
            final TabularType pageTabularType = new TabularType("List of Notification Agents", "asdf", pageType, itemNamesDescriptionsAndIndexName)
            tabularDataSupport = new TabularDataSupport(pageTabularType)

            for (final NotificationAgentWrapper wrapper : serviceManager.getNotificationAgents()) {

                final Statistics statistics = serviceManager.getStatistics(wrapper.getAgent().getClass().getCanonicalName(), MissionControlActor.RecordType.NOTIFICATION_AGENT).get()

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, [
                        wrapper.getAgent().getClass().getCanonicalName(),
                        wrapper.getDefinition().strategy().toString(),
                        Arrays.asList(wrapper.getDefinition().specifics()).toString(),
                        statistics.getDeliveredMessages(),
                        statistics.getProcessedMessages(),
                        statistics.getMessageExceptions(),
                        statistics.getAverageMessageProcessTime()]))
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Notification Agents", exception)
        }

        return tabularDataSupport
    }

    @Override
    public TabularDataSupport getPollResponseHandlers() {

        TabularDataSupport tabularDataSupport = null

        try {

            final String[] itemNamesDescriptionsAndIndexName = [
                    "Name",
                    "Strategy",
                    "Specifics",
                    "Number of Delivered Messages",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Avg Message Process Time (ms)"]

            final OpenType[] itemTypes = [
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.LONG]

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes)
            final TabularType pageTabularType = new TabularType("List of Poll Response Handlers", "asdf", pageType, itemNamesDescriptionsAndIndexName)
            tabularDataSupport = new TabularDataSupport(pageTabularType)

            for (final PollResponseWrapper wrapper : serviceManager.getPollResponseHandlers()) {

                final Statistics statistics = serviceManager.getStatistics(wrapper.getHandler().getClass().getCanonicalName(), MissionControlActor.RecordType.POLL_RESPONSE_HANDLER).get()

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, [
                        wrapper.getHandler().getClass().getCanonicalName(),
                        wrapper.getDefinition().strategy().toString(),
                        Arrays.asList(wrapper.getDefinition().specifics()).toString(),
                        statistics.getDeliveredMessages(),
                        statistics.getProcessedMessages(),
                        statistics.getMessageExceptions(),
                        statistics.getAverageMessageProcessTime()]))
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Poll Response Handlers", exception)
        }

        return tabularDataSupport
    }

    @Override
    public TabularDataSupport getRecordPersistenceServices() {

        TabularDataSupport tabularDataSupport = null

        try {

            final String[] itemNamesDescriptionsAndIndexName = [
                    "Name",
                    "Ranking",
                    "Number of Delivered Messages",
                    "Number of Processed Messages",
                    "Number of Message Exceptions",
                    "Avg Message Process Time (ms)"]

            final OpenType[] itemTypes = [
                    SimpleType.STRING,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.INTEGER,
                    SimpleType.LONG]

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes)
            final TabularType pageTabularType = new TabularType("List of Record Persistence Services", "asdf", pageType, itemNamesDescriptionsAndIndexName)
            tabularDataSupport = new TabularDataSupport(pageTabularType)

            for (final RecordPersistenceServiceWrapper wrapper : serviceManager.getRecordPersistenceServices()) {

                final Statistics statistics = serviceManager.getStatistics(wrapper.getService().getClass().getCanonicalName(), MissionControlActor.RecordType.RECORD_PERSISTENCE_SERVICE).get()

                tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, [
                        wrapper.getService().getClass().getCanonicalName(),
                        wrapper.getDefinition().ranking(),
                        statistics.getDeliveredMessages(),
                        statistics.getProcessedMessages(),
                        statistics.getMessageExceptions(),
                        statistics.getAverageMessageProcessTime()]))
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Record Persistence Services", exception)
        }

        return tabularDataSupport
    }

    @Override
    public TabularDataSupport getRecordsForMonitor(String monitoredService) {

        TabularDataSupport tabularDataSupport = null

        try {

            final String[] itemNamesDescriptionsAndIndexName = [
                    "Start Time",
                    "End Time",
                    "Runtime (ms)",
                    "Response",
                    "Stacktrace",
                    "Cleared"]

            final OpenType[] itemTypes = [
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.LONG,
                    SimpleType.STRING,
                    SimpleType.STRING,
                    SimpleType.BOOLEAN]

            final CompositeType pageType = new CompositeType("page", "Page size info", itemNamesDescriptionsAndIndexName, itemNamesDescriptionsAndIndexName, itemTypes)
            final TabularType pageTabularType = new TabularType("Records for Monitored Service", "asdf", pageType, itemNamesDescriptionsAndIndexName)
            tabularDataSupport = new TabularDataSupport(pageTabularType)

            Optional<RecordHolder> record = serviceManager.getRecordHolder(monitoredService)

            if (record.isPresent()) {

                for (final DetailedPollResponse detailedPollResponse : record.get().getRecords()) {

                    tabularDataSupport.put(new CompositeDataSupport(pageType, itemNamesDescriptionsAndIndexName, [
                            Constants.JMX_DATE_TIME_FORMATTER.format(detailedPollResponse.getStartTime()),
                            Constants.JMX_DATE_TIME_FORMATTER.format(detailedPollResponse.getStartTime()),
                            detailedPollResponse.runTimeInMilliseconds(),
                            detailedPollResponse.getResponseType().toString(),
                            detailedPollResponse.getStackTrace(),
                            detailedPollResponse.getCleared()]))
                }
            }

        } catch (final Exception exception) {

            LOG.error("An exception occurred building the TabularDataSupport listing the Monitor States", exception)
        }

        return tabularDataSupport
    }

    @Override
    public void resetAllAlarms() {
        serviceManager.resetAllAlarms()
    }

    @Override
    public void resetAlarm(final String fullyQualifiedMonitorPath) {
        serviceManager.resetAlarm(fullyQualifiedMonitorPath)
    }

}