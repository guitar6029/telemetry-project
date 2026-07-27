package com.joshsoll.telemetry.platform.metricvalue.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.metricdefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.metricvalue.constants.MetricValueConstants;
import com.joshsoll.telemetry.platform.telemetryevent.entity.TelemetryEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "metric_values")
public class MetricValue {

    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "telemetry_id", nullable = false)
    private TelemetryEvent telemetryEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_definition_id", nullable = false)
    private MetricDefinition metricDefinition;

    @Column(precision = MetricValueConstants.NUMBER_VALUE_PRECISION, scale = MetricValueConstants.NUMBER_VALUE_SCALE)
    private BigDecimal numberValue;

    @Column
    private Boolean booleanValue;

    @Column(length = MetricValueConstants.STRING_VALUE_MAX_LENGTH)
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
