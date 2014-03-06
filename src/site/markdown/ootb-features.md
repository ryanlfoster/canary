## Out-of-the-Box Features

In addition to the Canary Framework itself, there are numerous bundled, out-of-the-box features present in the project.
These features are implementations of the Canary API itself in the form of a few [Monitors](adding-a-monitor.html),
[Notification Agents](adding-a-notification-agent.html), and a JCR implementation of a
[Record Persistence Services](adding-a-persistence-service.html).

All of these features (except the JCR Persistence Manager) require a present [`sling:OsgiConfig`](http://dev.day.com/docs/en/cq/current/deploying/configuring_osgi.html)
 (see "OSGi Configuration in the Repository") in order to come online, be functional. Some features require parameters,
  which are defined in their specific configurations (see below). Otherwise a default configuration can and should be used.

    <?xml version="1.0" encoding="UTF-8"?>
    <jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
              jcr:primaryType="sling:OsgiConfig" />

## Monitors

1. [Blocked Agent Queue Monitor](groovydoc/com/citytechinc/canary/services/monitor/BlockedAgentQueueMonitor.html) - Examines all the configured replication agents, looking for blocked replication queues.
This monitor polls every 10 seconds and requires 2 minutes of failed polls before failing. Requires parameters:
    * `agentIds` - a list of strings which defaults to include 'publish'

2. [Log Listener Exception Collecting Monitor](groovydoc/com/citytechinc/canary/services/monitor/LogListenerExceptionCollectingMonitor.html) - Collects exception information and reports back when polled. This monitor
 polls every 10 seconds and requires 2 minutes of failed polls before failing. Requires parameters:
    * `scrutinizedPackagePaths` - a list of strings which represent packages and is empty by default.
     If the list contains no values, it will report all exceptions
    * `exceptionReportingThreshold` - an integer indicating a threshold required for a failed poll response

3. [Sling Event Job Monitor](groovydoc/com/citytechinc/canary/services/monitor/SlingEventJobMonitor.html) - Polls the sling [Job Manager](https://sling.apache.org/apidocs/sling6/org/apache/sling/event/jobs/JobManager.html)
 comparing the reported average wait and process times to the configured threshold limit. This monitor polls every
  10 seconds and requires 2 minutes of failed polls before failing. Requires parameters:
    * `averageProcessingTimeThreshold` - The average processing time threshold limit to exceed for an alarm
    * `averageWaitingTimeThreshold` - The average waiting time threshold limit to exceed for an alarm

## Notification Agents

1. [Email Notification](groovydoc/com/citytechinc/canary/services/notification/EmailNotificationAgent.html) - sends emails to a configured list of recipients from one sender using the
 [CQ MailService](http://dev.day.com/docs/en/cq/current/javadoc/com/day/cq/mailer/MailService.html). The CQ MailService
 dependency is required and if not met, this Notification Agent will be unsatisfied. Requires parameters:
    * `fromEmail` - the from e-mail address
    * `toEmailAddresses` - a list of strings which represent the 'to receive' e-mail addresses

2. [Log Escalation Notification Agent](groovydoc/com/citytechinc/canary/services/notification/LogEscalationNotificationAgent.html) - creates a configuration entry via the
[Configuration Admin](http://www.osgi.org/javadoc/r4v42/org/osgi/service/cm/ConfigurationAdmin.html) for the Monitor.
This agent will also clean up after itself when alarms are cleared.

## Persistence Services

1. [JCR Persistence Manager](groovydoc/com/citytechinc/canary/services/persistence/JCRPersistenceManager.html) - responsible for persisting and retrieving Monitor records from the JCR.