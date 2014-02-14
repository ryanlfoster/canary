## Adding a monitor
Monitors are used in the polling process within the framework. This functionality can implemented for existing services
or it can be created for the sole purpose of monitoring and reporting.

The minimum requirements for adding a monitor are:

* Implement the [MonitoredService](apidocs/com/citytechinc/canary/api/monitor/MonitoredService.html) interface. This interface is what the Apache Felix OSGi container will use to bind your monitor to the ServiceManager. The implemtnation is rather straight forward, with a single `poll()` method that returns a [PollResponse](apidocs/com/citytechinc/canary/api/monitor/PollResponse.html)
* Provide registration details via the [MonitoredServiceDefinition](apidocs/com/citytechinc/canary/api/monitor/MonitoredServiceDefinition.html) annotation. There are a number of configurable options, three of which are required...
    * `name` a String (required)
    * `description` a String
    * `pollInterval` an int (required)
    * `pollIntervalUnit` a TimeUnit (required)
    * `alarmThreshold` an int
    * `maxNumberOfRecords` an int
    * `persistWhenAlarmed` a boolean
    * `maxExecutionTimeInMillseconds`

#### Example monitor that polls once a second

    package com.citytechinc.canary.samples.monitor

    import com.citytechinc.canary.api.monitor.MonitoredService
    import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
    import com.citytechinc.canary.api.monitor.PollResponse
    import org.apache.felix.scr.annotations.Component
    import org.apache.felix.scr.annotations.Service

    import java.util.concurrent.TimeUnit

    @Component(immediate = true)
    @Service
    @MonitoredServiceDefinition(name = 'Example Monitor', pollInterval = 1, pollIntervalUnit = TimeUnit.SECONDS)
    class ExampleMonitor implements MonitoredService {

    @Override
    PollResponse poll() {
    PollResponse.SUCCESS()
    }
    }

### Defining Automatic Reset Monitor
[AutomaticResetMonitor](apidocs/com/citytechinc/canary/api/AutomaticResetMonitor.html) is a feature intended to aid in a situation where an engineer might be unable to tend to an alarmed
monitor for an extended period of time. Normally, once alarmed, a monitor can only be reset by an explicit call. This optional
annotation allows for the definition of a secondary polling period and required number of *healthy* polls before resetting the
alarm and resuming normal polling.

### Defining Log Escalation
[EscalatingLogMonitor](apidocs/com/citytechinc/canary/api/EscalatingLogMonitor.html) instructs the framework to generate an elevated logger confiruation explicitly for the alarmed service.
If a custom logger configuration exists for the monitored service, it will be modified and set to the elevated level.
If no logger confirations are found, a new one will be explicitly created based on the `prefix` defined in the annotation.

#### Example monitor that polls once a second, auto resets after 6 successful, 1 minute interval polls, and enables log escalation

    package com.citytechinc.canary.samples.monitor

    import com.citytechinc.canary.api.monitor.AutomaticResetMonitor
    import com.citytechinc.canary.api.monitor.LogEscalatingMonitor
    import com.citytechinc.canary.api.monitor.MonitoredService
    import com.citytechinc.canary.api.monitor.MonitoredServiceDefinition
    import com.citytechinc.canary.api.monitor.PollResponse
    import org.apache.felix.scr.annotations.Component
    import org.apache.felix.scr.annotations.Service

    import java.util.concurrent.TimeUnit

    @Component(immediate = true)
    @Service
    @MonitoredServiceDefinition(name = 'Example Monitor', pollInterval = 1, pollIntervalUnit = TimeUnit.SECONDS)
    @AutomaticResetMonitor(interval = 1, unit = TimeUnit.MINUTES)
    @LogEscalatingMonitor(prefixPathAndName = 'monitorlogs/examplemonitor-')
    class ExampleMonitor implements MonitoredService {

    @Override
    PollResponse poll() {
    PollResponse.SUCCESS()
    }
    }