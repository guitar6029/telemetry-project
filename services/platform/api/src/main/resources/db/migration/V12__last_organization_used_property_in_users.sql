ALTER TABLE users
ADD COLUMN last_organization_used UUID;

ALTER TABLE users
ADD CONSTRAINT fk_users_last_organization_used
FOREIGN KEY (last_organization_used)
REFERENCES organizations(id)
ON DELETE SET NULL;
