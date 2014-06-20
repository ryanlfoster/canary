## Adding a monitor

As mentioned in the [Overview](index.html), Monitors are the source of data for the Canary Framework. Monitors are polled
by the framework based on their specific configurations -- an interval and a time unit (seconds, minutes, etc...). Monitors
can poll up to once per second. Any configuration unit smaller than a single second will be rounded up to a second. If
 a poll exceeds the maximum execution time, it will be interrupted and a failure will
be recorded indicting the interruption. Poll responses are recorded in-memory and routed to Mission Control for distribution to all
[Poll Response Handlers](adding-a-response-handler.html).

In order to detect an alarm state, poll data is analyzed each time a poll is pushed into the in-memory record storage.
The configured alarm threshold and criteria are used to determine and flag an alarm state. Monitors support alarm
 criteria: `Recent Polls`, `Average Execution Time`, or `Average Failure Rate`. Note: The averaged alarm criteria are
 affected by the max number of records configured for the monitor.

If an alarm state is detected, the entire in-memory set of records is routed to Mission Control for distribution to all
[Notification Agents](adding-a-notification-agent.html). If the monitor is configured to persist when alarmed, the same
record set sent to the [Notification Agents](adding-a-notification-agent.html) is additionally routed to all
 [Persistence Services](adding-a-persistence-service.html).

#### Basic, minimum configuration

1. It implements [`MonitoredService`](groovydoc/com/citytechinc/aem/canary/api/monitor/MonitoredService.html) interface.
 The `MonitoredService` interface defines a single, zero argument method, `poll()` which has a return type of
  [`PollResponse`](groovydoc/com/citytechinc/aem/canary/api/monitor/PollResponse.html)
2. It defines or has the [MonitoredServiceDefinition](groovydoc/com/citytechinc/aem/canary/api/monitor/MonitoredServiceDefinition.html)
  annotation (with required values). The annotation is used to define all aspects of the monitor within the Canary Framework, which include...

    * `name` a String (required)
    * `description` a String
    * `pollInterval` an int (required)
    * [`pollIntervalUnit`](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html)
    * [`alarmCriteria`](groovydoc/com/citytechinc/aem/canary/api/monitor/AlarmCriteria.html)
    * `alarmThreshold` an int
    * `maxNumberOfRecords` an int
    * `persistWhenAlarmed` a boolean
    * `maxExecutionTime` - The maximum allowed execution time before for a given poll in milliseconds
    * `logEscalation` - Will

#### Additional configuration annotations

* There is an additional annotation, [`AutomaticResetMonitor`](groovydoc/com/citytechinc/aem/canary/api/monitor/AutomaticResetMonitor.html), which, when supplied
 enables polling while a monitor is alarmed. The annotation takes two, required arguments...

    1. resetInterval
    2. [`resetIntervalUnit`](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html)