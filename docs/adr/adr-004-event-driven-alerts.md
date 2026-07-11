## Alerts Are Triggered By Events

Context

Template updates should not unexpectedly generate alerts.

Decision

Alerts are evaluated only when telemetry events arrive.

Configuration changes affect future evaluations only.

Historical reprocessing is an explicit operation.
