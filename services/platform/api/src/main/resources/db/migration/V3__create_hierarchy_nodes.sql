CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE
    hierarchy_nodes (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        name VARCHAR(50) NOT NULL,
        organization_id UUID NOT NULL,
        parent_node_id UUID,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        CONSTRAINT fk_hierarchy_node_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
        CONSTRAINT fk_hierarchy_node_parent FOREIGN KEY (parent_node_id) REFERENCES hierarchy_nodes (id)
    );