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

# Telemetry Processing Pipeline

## Goal

Convert raw telemetry JSON into normalized MetricValue records.

## Steps

1. Extract rawPayload
2. Parse JSON into Map<String, Object>
3. Load DeviceTemplate
4. Load MetricDefinitions
5. Build lookup map
6. Iterate payload
7. Match incoming field
8. Validate data type
9. Create MetricValue
10. Persist MetricValue

## Why use Map<String, MetricDefinition>?

Converting the list into a lookup map changes repeated searches from O(n) to O(1), making payload processing more efficient.
