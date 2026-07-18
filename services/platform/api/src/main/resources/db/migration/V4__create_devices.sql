CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE
    devices (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        name VARCHAR(50) NOT NULL,
        manufacturer VARCHAR(50) NOT NULL,
        model VARCHAR(50) NOT NULL,
        serial_number VARCHAR(50) NOT NULL,
        firmware_version VARCHAR(50) NOT NULL,
        status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE', -- Stores 'ONLINE', 'OFFLINE', 'MAINTENANCE', 'RETIRED' 
        device_template_id UUID NOT NULL,
        organization_id UUID NOT NULL,
        hierarchy_node_id UUID NOT NULL,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        CONSTRAINT fk_device_device_template FOREIGN KEY (device_template_id) REFERENCES device_templates (id),
        CONSTRAINT fk_device_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
        CONSTRAINT fk_device_hierarchy_node FOREIGN KEY (hierarchy_node_id) REFERENCES hierarchy_nodes (id)
    );