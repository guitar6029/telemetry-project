CREATE TABLE device_imports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    device_template_id UUID NOT NULL,
    hierarchy_node_id UUID NOT NULL,
    filename TEXT NOT NULL,
    import_mode VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    submitted_at TIMESTAMPTZ NOT NULL,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    CONSTRAINT fk_device_import_organization
        FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_device_import_device_template
        FOREIGN KEY (device_template_id) REFERENCES device_templates(id),
    CONSTRAINT fk_device_import_hierarchy_node
        FOREIGN KEY (hierarchy_node_id) REFERENCES hierarchy_nodes(id)
);
