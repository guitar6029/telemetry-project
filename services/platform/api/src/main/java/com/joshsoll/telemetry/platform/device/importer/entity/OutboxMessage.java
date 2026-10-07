package com.joshsoll.telemetry.platform.device.importer.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_messages")
public class OutboxMessage {

    @Id
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;

    protected OutboxMessage() {
    }

    public OutboxMessage(String eventType, UUID aggregateId, String payload, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void markPublished(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
