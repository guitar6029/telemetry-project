CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE
    device_templates (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        organization_id UUID NOT NULL,
        name VARCHAR(50) NOT NULL,
        description VARCHAR(255) DEFAULT NULL,
        archived BOOLEAN NOT NULL DEFAULT FALSE,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        CONSTRAINT fk_device_template_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
    );