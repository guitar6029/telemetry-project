## Known Future Concern

External systems may expose telemetry using arbitrary payload
structures and naming conventions.

The core telemetry platform assumes a normalized telemetry contract.

If customer demand requires greater flexibility, introduce a
dedicated Integration/Mapping layer rather than embedding payload
translation into the telemetry engine.
