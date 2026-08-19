CREATE TABLE device_import_artifacts (
    id UUID PRIMARY KEY,
    device_import_id UUID NOT NULL UNIQUE,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    content BYTEA NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_device_import_artifact_import
        FOREIGN KEY (device_import_id)
        REFERENCES device_imports(id)
        ON DELETE CASCADE
);
