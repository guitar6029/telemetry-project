## Configuration vs Runtime

One of the core architectural principles of the telemetry platform is the separation of configuration from runtime data.

Configuration

These objects are relatively static and change infrequently.

```
Organization
Hierarchy
Device
DeviceTemplate
MetricDefinition
Runtime
```

These objects represent live system activity.

```
TelemetryEvent
MetricValue
MetricEvent
Notifications
Analytics
Dashboards
```

## Configuration describes the system.

Runtime reflects what is currently happening.

Single Responsibility

MetricDefinition has one responsibility:

Describe a metric.

It is responsible for defining:

```
Name
Description
Incoming field name
Data type
Unit
Device Template relationship
```

It is not responsible for:

```
Storing values
Evaluating thresholds
Generating alerts
Triggering notifications
Performing analytics
Rendering dashboards
```

Those responsibilities belong to separate services and domains.

## Canonical Source of Truth

Many parts of the platform consume MetricDefinition.

For example:

```
Telemetry Ingestion
        │
Validation
        │
Event Engine
        │
Analytics
        │
Dashboard

```

Although many services depend on MetricDefinition, it remains focused on describing the metric rather than performing those operations.

## Metric Lifecycle

A metric definition can exist long before any telemetry is received.

Example:

```
Battery

Incoming Field : battery
Display Name   : Battery
Data Type      : NUMBER
Unit           : %

```

Initially there may be zero telemetry values.

Once devices begin reporting, runtime services use the definition to interpret incoming values.

```
Relationship Model
DeviceTemplate
        │
        ▼
MetricDefinition
        │
        ▼
Runtime Services
        ├── Telemetry
        ├── Validation
        ├── Events
        ├── Analytics
        └── Dashboards

```

This keeps the domain loosely coupled while allowing multiple services to consume the same configuration.
