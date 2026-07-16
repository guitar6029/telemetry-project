# Parent / Child Persistence

Problem

TransientPropertyValueException

Reason

MetricValue referenced a TelemetryEvent that had not yet been persisted.

Incorrect

Create TelemetryEvent

↓

Process

↓

Save TelemetryEvent

Correct

Create TelemetryEvent

↓

Save TelemetryEvent

↓

Process

↓

Save MetricValues

Lesson

Persist parent entities before persisting children that reference them.
