CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE
    metric_values (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        telemetry_id UUID NOT NULL,
        metric_definition_id UUID NOT NULL,
        number_value NUMERIC(19, 6),
        boolean_value BOOLEAN,
        string_value VARCHAR(255),
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        CONSTRAINT fk_metric_value_telemetry FOREIGN KEY (telemetry_id) REFERENCES telemetry_events (id),
        CONSTRAINT fk_metric_value_metric_definition FOREIGN KEY (metric_definition_id) REFERENCES metric_definitions (id)
    )