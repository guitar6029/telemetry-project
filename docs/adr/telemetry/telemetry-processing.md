## Telemetry Processing Pipeline

```
TelemetryEvent
        ↓
Extract rawPayload
        ↓
Parse JSON (ObjectMapper)
        ↓
Map<String, Object>
        ↓
Load MetricDefinitions
        ↓
Convert List -> Lookup Map
        ↓
Iterate payload
        ↓
Find MetricDefinition
        ↓
Unknown?
        ↓
Log + Continue
        ↓
Validate MetricDataType
        ↓
Invalid?
        ↓
Log + Continue
        ↓
Create MetricValue
        ↓
Persist
```

Why use a Map?

Instead of

```
Payload

↓

Search List

↓

Search List

↓

Search List

```

we build

```
Map<String, MetricDefinition>

O(n)

instead of

O(n²)
```

Entity Relationship Decision

Originally

```
TelemetryEvent

↓

deviceId
```

Later

```
TelemetryEvent

↓

Device
```

Reason:

internal navigation
avoid unnecessary repository lookups
expose only when a real domain use case appears

Design Principles

Today's session reinforced several principles:

Guard clauses keep the happy path readable.
Validate keys before values.
Normalize incoming data into the platform's canonical types.
Keep the controller thin; orchestration belongs in the service.
Prefer lookup maps when repeatedly searching by a key.
Expose entity relationships only when there is a legitimate domain use case.
