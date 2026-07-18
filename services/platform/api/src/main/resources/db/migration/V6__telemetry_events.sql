CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE
    telemetry_events (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        device_id UUID NOT NULL,
        raw_payload TEXT,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        received_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        CONSTRAINT fk_telemetry_device FOREIGN KEY (device_id) REFERENCES devices (id)
    );