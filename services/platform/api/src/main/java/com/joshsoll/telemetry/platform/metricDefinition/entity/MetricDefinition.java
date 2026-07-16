package com.joshsoll.telemetry.platform.metricDefinition.entity;

import java.time.Instant;
import java.util.UUID;

import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.metricDefinition.MetricDataType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "metric_definitions")
public class MetricDefinition {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    private String incomingFieldName;

    @Enumerated(EnumType.STRING)
    private MetricDataType dataType;

    private String unit;

    @ManyToOne
    private DeviceTemplate deviceTemplate;

    private Instant createdAt;
    private Instant updatedAt;

    protected MetricDefinition() {
    }

    public MetricDefinition(
            String name,
            String description,
            String incomingFieldName,
            MetricDataType dataType,
            String unit,
            DeviceTemplate deviceTemplate,
            Instant createdAt,
            Instant updatedAt) {
        this.name = name;
        this.description = description;
        this.incomingFieldName = incomingFieldName;
        this.dataType = dataType;
        this.unit = unit;
        this.deviceTemplate = deviceTemplate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getDeviceTemplateId() {
        return deviceTemplate.getId();
    }

    public String getIncomingFieldName() {
        return incomingFieldName;
    }

    public MetricDataType getDataType() {
        return dataType;
    }

    public String getUnit() {
        return unit;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
