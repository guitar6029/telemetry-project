CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE invitations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL,

    email VARCHAR(255) NOT NULL,

    role VARCHAR(50) NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    expires_at TIMESTAMPTZ NOT NULL
);


CREATE INDEX idx_invitation_org
    ON invitations (organization_id);

CREATE INDEX idx_invitation_org_email_status
    ON invitations (organization_id, email, status);

CREATE INDEX idx_invitation_status_expires
    ON invitations (status, expires_at);
