ALTER TABLE devices
    ADD CONSTRAINT uq_device_organization_serial_number
    UNIQUE (organization_id, serial_number);
