CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE device_imports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    status VARCHAR(30) NOT NULL DEFAULT 'PREVIEW',
    mode VARCHAR(30) NOT NULL DEFAULT 'SKIP_EXISTING',

    organization_id UUID NOT NULL,
    template_id UUID NOT NULL,
    hierarchy_node_id UUID NOT NULL,

    total_rows INTEGER NOT NULL DEFAULT 0,
    valid_rows INTEGER NOT NULL DEFAULT 0,
    invalid_rows INTEGER NOT NULL DEFAULT 0,

    expires_at TIMESTAMPTZ NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_device_import_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_device_import_template
        FOREIGN KEY (template_id)
        REFERENCES device_templates(id),

    CONSTRAINT fk_device_import_hierarchy_node
        FOREIGN KEY (hierarchy_node_id)
        REFERENCES hierarchy_nodes(id)
);
