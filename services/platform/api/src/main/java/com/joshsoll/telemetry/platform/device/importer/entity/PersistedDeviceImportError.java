package com.joshsoll.telemetry.platform.device.importer.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportError;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "device_import_errors")
public class PersistedDeviceImportError {

    @Id
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_import_id", nullable = false)
    private DeviceImport deviceImport;

    @Column(name = "row_number", nullable = false)
    private long rowNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> messages;

    protected PersistedDeviceImportError() { }

    public PersistedDeviceImportError(DeviceImport deviceImport, DeviceImportError error) {
        this.id = UUID.randomUUID();
        this.deviceImport = deviceImport;
        this.rowNumber = error.rowNumber();
        this.messages = List.copyOf(error.errors());
    }

    public long getRowNumber() { return rowNumber; }
    public List<String> getMessages() { return messages; }
}
