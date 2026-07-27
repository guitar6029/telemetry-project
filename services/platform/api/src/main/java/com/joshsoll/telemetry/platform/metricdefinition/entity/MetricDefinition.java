package com.joshsoll.telemetry.platform.metricdefinition.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.metricdefinition.MetricDataType;
import com.joshsoll.telemetry.platform.metricdefinition.constants.MetricDefinitionConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "metric_definitions")
public class MetricDefinition {
    @Id
    @Generated
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, length = MetricDefinitionConstants.NAME_MAX_LENGTH)
    private String name;

    @Column(nullable = false, length = MetricDefinitionConstants.DESCRIPTION_MAX_LENGTH)
    private String description;

    @Column(nullable = false, length = MetricDefinitionConstants.INCOMING_FIELD_NAME_MAX_LENGTH)
    private String incomingFieldName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = MetricDefinitionConstants.DATA_TYPE_MAX_LENGTH)
    private MetricDataType dataType;

    @Column(length = MetricDefinitionConstants.UNIT_MAX_LENGTH)
    private String unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "device_template_id")
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
