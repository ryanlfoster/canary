package com.citytechinc.canary.services.manager.actors

import com.citytechinc.canary.api.notification.AlarmNotification
import com.citytechinc.canary.api.notification.AlarmResetNotification
import com.citytechinc.canary.api.notification.NotificationAgentWrapper
import com.citytechinc.canary.api.notification.SubscriptionStrategy
import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor
import org.apache.sling.commons.scheduler.Scheduler

import java.util.concurrent.TimeUnit

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 */
@Slf4j
final class NotificationAgentActor extends DynamicDispatchActor {

    static String JOB_PREFIX = 'canaryna-'

    // MESSAGES
    static class FlushQueue { }

    NotificationAgentWrapper wrapper
    Scheduler scheduler
    Statistics statistics = new Statistics()

    void afterStop() {

        scheduler.removeJob(JOB_PREFIX + wrapper.identifier)
    }

    Map<String, AlarmNotification> queuedAlarmNotifications = [:]
    Map<String, AlarmResetNotification> queuedAlarmResetNotifications = [:]

    void onMessage(MissionControlActor.GetStatistics message) {
        sender.send(statistics.clone())
    }

    void onMessage(AlarmNotification message) {

        ++statistics.deliveredMessages

        if (processMessage(message.recordHolder.monitorIdentifier)) {

            if (wrapper.aggregationCriteriaDefined) {

                if (queuedAlarmNotifications.isEmpty() && queuedAlarmResetNotifications.isEmpty()) {

                    Date now = new Date()
                    scheduler.fireJobAt(JOB_PREFIX + wrapper.identifier, {

                        this << new FlushQueue()

                    }, [:], new Date(now.time + TimeUnit.MILLISECONDS.convert(wrapper.aggregationWindow, wrapper.aggregationWindowTimeUnit)))
                }

                log.debug("Adding alarm message to queue with size of ${queuedAlarmNotifications.size()}")
                queuedAlarmNotifications.put(message.recordHolder.monitorIdentifier, message)

            } else {

                log.debug("Aggregation undefined, sending alarm message without delay...")

                Stopwatch stopwatch = Stopwatch.createStarted()

                try {

                    wrapper.handleAlarmNotification([message])
                    ++statistics.processedMessages

                } catch (Exception e) {

                    log.error("An exception occurred calling the notification agent: ${wrapper.identifier} for service: ${message.recordHolder.monitorIdentifier}", e)
                    ++statistics.messageExceptions
                }

                Long processTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)
                log.debug("It took ${processTime} ms to call the notification agent: ${wrapper.identifier} for service: ${message.recordHolder.monitorIdentifier}")

                statistics.addAndCalculateAverageProcessTime(processTime)
            }
        }
    }

    void onMessage(AlarmResetNotification message) {

        ++statistics.deliveredMessages

        if (processMessage(message.recordHolder.monitorIdentifier)) {

            if (wrapper.aggregationCriteriaDefined) {

                if (queuedAlarmNotifications.isEmpty() && queuedAlarmResetNotifications.isEmpty()) {

                    Date now = new Date()
                    scheduler.fireJobAt(JOB_PREFIX + wrapper.identifier, {

                        this << new FlushQueue()

                    }, [:], new Date(now.time + TimeUnit.MILLISECONDS.convert(wrapper.aggregationWindow, wrapper.aggregationWindowTimeUnit)))
                }

                log.debug("Adding alarm reset message to queue with size of ${queuedAlarmResetNotifications.size()}")
                queuedAlarmResetNotifications.put(message.recordHolder.monitorIdentifier, message)

            } else {

                log.debug("Aggregation undefined, sending alarm reset message without delay...")

                Stopwatch stopwatch = Stopwatch.createStarted()

                try {

                    wrapper.handleAlarmNotification([message])
                    ++statistics.processedMessages

                } catch (Exception e) {

                    log.error("An exception occurred calling the notification agent: ${wrapper.identifier} for service: ${message.recordHolder.monitorIdentifier}", e)
                    ++statistics.messageExceptions
                }

                Long processTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)
                log.debug("It took ${processTime} ms to call the notification agent: ${wrapper.identifier} for service: ${message.recordHolder.monitorIdentifier}")

                statistics.addAndCalculateAverageProcessTime(processTime)
            }
        }
    }

    Boolean processMessage(String identifier) {
        (((wrapper.strategy == SubscriptionStrategy.OPT_INTO) && (wrapper.specifics.contains(identifier)))
            || ((wrapper.strategy == SubscriptionStrategy.OPT_OUT_OF) && (!wrapper.specifics.contains(identifier)))
            || (wrapper.strategy == SubscriptionStrategy.ALL))
    }

    void onMessage(FlushQueue message) {

        log.debug("Flushing queues alarms: ${queuedAlarmNotifications.size()} and alarm resets: ${queuedAlarmResetNotifications.size()}")

        Stopwatch stopwatch = Stopwatch.createStarted()

        try {

            def alarmNotifications = queuedAlarmNotifications.values() as List<AlarmNotification>
            def alarmResetNotifications = queuedAlarmResetNotifications.values() as List<AlarmResetNotification>

            if (!alarmNotifications.isEmpty()) {
                wrapper.handleAlarmNotification(alarmNotifications)
            }

            if (!alarmResetNotifications.isEmpty()) {
                wrapper.handleAlarmResetNotification(alarmResetNotifications)
            }

            ++statistics.processedMessages

        } catch (Exception e) {

            log.error("An exception occurred while flushing the message queues, calling the notification agent: ${wrapper.identifier}" +
                    " sending alarm notifications for services: ${queuedAlarmNotifications.values()*.recordHolder.monitorIdentifier}" +
                    " and alarm reset notifications for services: ${queuedAlarmResetNotifications.values()*.recordHolder.monitorIdentifier}", e)
            ++statistics.messageExceptions
        }

        queuedAlarmNotifications.clear()
        queuedAlarmResetNotifications.clear()

        Long processTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)
        log.debug("It took ${processTime} ms to flush the message queues, call the notification agent: ${wrapper.identifier}" +
                " sending alarm notifications for services: ${queuedAlarmNotifications.values()*.recordHolder.monitorIdentifier}" +
                " and alarm reset notifications for services: ${queuedAlarmResetNotifications.values()*.recordHolder.monitorIdentifier}")

        statistics.addAndCalculateAverageProcessTime(processTime)
    }

}
