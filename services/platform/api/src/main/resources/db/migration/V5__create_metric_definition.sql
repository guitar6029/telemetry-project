CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE
    metric_definitions (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        name VARCHAR(50) NOT NULL,
        description VARCHAR(100) NOT NULL,
        incoming_field_name VARCHAR(100) NOT NULL,
        data_type VARCHAR(20) NOT NULL,
        unit VARCHAR(25),
        device_template_id UUID NOT NULL,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        CONSTRAINT fk_metric_definition_device_template FOREIGN KEY (device_template_id) REFERENCES device_templates (id)
    )