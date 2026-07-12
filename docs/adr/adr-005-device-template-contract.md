## Device Templates Define Telemetry Contracts

Context

The platform supports many different industries.

Devices produce different telemetry depending on the customer's domain.

Hardcoding telemetry fields into the Device entity would tightly couple the platform to specific industries and make future expansion difficult.

Decision

Device Templates define the telemetry contract for one or more devices.

A template owns its Metric Definitions.

Devices reference a template rather than defining telemetry directly.

Templates are intended to be reusable across many devices.

Consequences
Pros
Generic platform
Reusable configurations
Consistent validation
Easy onboarding of hundreds of devices
Cons
Customers must configure templates before telemetry can be validated.
Introduces another domain object.
