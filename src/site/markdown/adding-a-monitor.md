## Adding a monitor

As mentioned in the [Overview](index.html), Monitors are the source of data for the Canary Framework. Monitors are polled
by the framework and their data is kept in-memory. The poll data is interpreted by the internal mechanisms and reported
back through the framework as defined by the configuration.

Developers can register a monitor by modifying or creating an AEM OSGi component such that...

#### Basic, minimum configuration

1. It implements [`MonitoredService`](groovydoc/com/citytechinc/canary/api/monitor/MonitoredService.html) interface.
 The `MonitoredService` interface defines a single, zero argument method, `poll()` which has a return type of
  [`PollResponse`](groovydoc/com/citytechinc/canary/api/monitor/PollResponse.html)
2. It defines or has the [MonitoredServiceDefinition](groovydoc/com/citytechinc/canary/api/monitor/MonitoredServiceDefinition.html)
  annotation (with required values). The annotation is used to define all aspects of the monitor within the Canary Framework, which include...

    * `name` a String (required)
    * `description` a String
    * `pollInterval` an int (required)
    * [`pollIntervalUnit`](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html)
    * [`alarmCriteria`](groovydoc/com/citytechinc/canary/api/monitor/AlarmCriteria.html)
    * `alarmThreshold` an int
    * `maxNumberOfRecords` an int
    * `persistWhenAlarmed` a boolean
    * `maxExecutionTime` - The maximum allowed execution time before for a given poll in milliseconds
    * `logEscalation` - Will

#### Additional configuration annotations

* There is an additional annotation, [`AutomaticResetMonitor`](groovydoc/com/citytechinc/canary/api/monitor/AutomaticResetMonitor.html), which, when supplied
 enables polling while a monitor is alarmed. The annotation takes two, required arguments...

    1. resetInterval
    2. [`resetIntervalUnit`](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html)