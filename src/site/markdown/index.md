## Overview

The Canary framework is a simple, stand alone monitoring, profiling, and reporting tool for OSGi components and
services within AEM. The framework enables scheduled, custom monitoring or profiling of critical aspects of an AEM
instance. Canary provides a conduit between Monitors, which collect data, and other registered service types that
can distribute and execute on that data. In addition to polling and distribution, Canary provides an API for talking
 to services in the framework, JMX beans for reporting and maintenance, and a rich UI for representing this data.

## Details

The AEM Canary framework consists of registered [Monitor(s)](adding-a-monitor.html) that provide responses to poll requests. These poll requests are
collected and analyzed in the framework based on supplied configurations.

The analysis for poll requests can invoke any of the following other extensible service types:

* [Poll Response Handlers](adding-a-response-handler.html) - All poll responses from Monitors are routed to all registered Poll Response Handlers. Handlers can choose
 to opt into or out of messages based off specific monitor paths.
* [Notification Agents](adding-a-notification-agent.html) - Receive the entire set of in-memory records for a Monitor when the individual Monitor becomes alarmed or
 has its alarm reset. Notification Agents also allow for aggregation. When enabled, aggregation allows the agent to collect
 notifications for a configurable period of time. Once expired, or flushed, these messages are delivered to their destination in a single call.
* [Record Persistence Services](adding-a-persistence-service.html) - Responsible for the persistence and retrieval of every Monitor's data. The intent of Record
 Persistence Services is to provide persistent storage.

## Requirements

* Adobe/Day CQ (Adobe AEM) 5.6

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.

## Issues

Please contact [Josh Durbin](mailto:jdurbin@citytechinc.com) with any questions; issues can be submitted via
[GitHub](https://github.com/Citytechinc/canary/issues) or via [waffle.io](https://waffle.io/citytechinc/canary).
