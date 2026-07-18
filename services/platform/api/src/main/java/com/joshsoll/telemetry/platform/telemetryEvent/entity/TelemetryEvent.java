package com.joshsoll.telemetry.platform.telemetryEvent.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.device.entity.Device;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "telemetry_events")
public class TelemetryEvent {

    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    private String rawPayload;

    private Instant createdAt;

    private Instant receivedAt;

    protected TelemetryEvent() {
    }

    public TelemetryEvent(

            Device device,

            String rawPayload,

            Instant createdAt,

            Instant receivedAt

    ) {

        this.device = device;
        this.rawPayload = rawPayload;
        this.createdAt = createdAt;
        this.receivedAt = receivedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getDeviceId() {
        return device.getId();
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public Device getDevice() {
        return device;
    }
}
