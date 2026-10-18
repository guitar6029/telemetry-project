ALTER TABLE device_imports
    ADD COLUMN total_rows BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN created_rows BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN updated_rows BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN skipped_rows BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN failed_rows BIGINT NOT NULL DEFAULT 0;

CREATE TABLE device_import_errors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_import_id UUID NOT NULL,
    row_number BIGINT NOT NULL,
    messages JSONB NOT NULL,
    CONSTRAINT fk_device_import_error_import
        FOREIGN KEY (device_import_id) REFERENCES device_imports(id) ON DELETE CASCADE
);

CREATE INDEX idx_device_import_errors_import_row
    ON device_import_errors (device_import_id, row_number);
