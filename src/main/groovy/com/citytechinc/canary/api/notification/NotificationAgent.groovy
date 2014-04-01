package com.citytechinc.canary.api.notification

/**
 *
 * @author Josh Durbin, CITYTECH, Inc. 2013
 *
 * Copyright 2013 CITYTECH, Inc.
 *
 * Notification agents receive the entire record set, the record holder, for a monitor that has transitioned to an
 *   alarmed state or been cleared of its alarm state. Also necessary for downstream processing is the monitor
 *   definition, which is also passed as part of the AlarmNotification or AlarmResetNotification.
 *
 */
public interface NotificationAgent {

    /**
     *
     * @param alarmNotifications
     */
    public void handleAlarmNotification(List<AlarmNotification> alarmNotifications)

    /**
     *
     * @param alarmResetNotifications
     */
    public void handleAlarmResetNotification(List<AlarmResetNotification> alarmResetNotifications)
}
