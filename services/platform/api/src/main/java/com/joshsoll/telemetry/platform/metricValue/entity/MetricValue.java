package com.joshsoll.telemetry.platform.metricValue.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.metricDefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.telemetryEvent.entity.TelemetryEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "metric_values")
public class MetricValue {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private TelemetryEvent telemetryEvent;

    @ManyToOne
    private MetricDefinition metricDefinition;

    @Column(precision = 19, scale = 6)
    private BigDecimal numberValue;

    private Boolean booleanValue;

    private String stringValue;

    private Instant createdAt;

    protected MetricValue() {
    }

    public MetricValue(

            TelemetryEvent telemetryEvent,

            MetricDefinition metricDefinition,

            BigDecimal numberValue,

            Boolean booleanValue,

            String stringValue,

            Instant createdAt

    ) {

        this.telemetryEvent = telemetryEvent;
        this.metricDefinition = metricDefinition;
        this.numberValue = numberValue;
        this.booleanValue = booleanValue;
        this.stringValue = stringValue;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTelemetryEventId() {
        return telemetryEvent.getId();
    }

    public UUID getMetricDefinitionId() {
        return metricDefinition.getId();
    }

    public BigDecimal getNumberValue() {
        return numberValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
