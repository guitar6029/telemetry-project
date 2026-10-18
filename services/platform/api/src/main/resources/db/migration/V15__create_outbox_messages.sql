CREATE TABLE outbox_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type VARCHAR(50) NOT NULL,
    aggregate_id UUID NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,
    CONSTRAINT fk_outbox_message_device_import
        FOREIGN KEY (aggregate_id) REFERENCES device_imports(id)
);

CREATE INDEX idx_outbox_messages_unpublished
    ON outbox_messages (created_at)
    WHERE published_at IS NULL;
