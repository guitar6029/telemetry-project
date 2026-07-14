Context

We're building the runtime portion of the telemetry platform.

The system needs to persist telemetry received from devices while keeping the domain model cohesive and avoiding unnecessary coupling between data storage and processing logic.

Decision

TelemetryEvent will represent the original telemetry transmission received from a device.

It is designed as an immutable business record ("envelope") whose responsibility is to preserve the original payload and metadata about its arrival.

Processing, validation, alerting, analytics, and other downstream concerns are intentionally excluded from the entity.

Responsibilities
Store the originating device.
Store the original payload.
Record when the payload was received.
Preserve the original transmission for future auditing and troubleshooting.
Non-Responsibilities

TelemetryEvent does not:

Parse telemetry values.
Validate telemetry.
Generate alerts.
Trigger notifications.
Track processing state.
Understand messaging infrastructure (Kafka, RabbitMQ, etc.).
Store retry information.

Those responsibilities belong to application services.

Consequences

Benefits

Clear separation of concerns.
Simple domain entity.
Easier future migration to asynchronous processing.
Historical record of original device transmissions.
Supports future reprocessing of telemetry.

Trade-offs

Processing state must be tracked elsewhere if needed.
Additional services coordinate the telemetry pipeline.
Notes

Future implementations may introduce:

Kafka
RabbitMQ
AWS SQS
TelemetryProcessingService

These components transport or process telemetry but do not change the responsibility of TelemetryEvent.

### TelemetryEvent is an envelope, not the postal system.

During design discussions, TelemetryEvent was compared to an envelope moving through a postal system. The envelope simply contains information. The postal system (processing services) determines what happens to it. This analogy guided the decision to keep the entity free of processing state and workflow logic.
