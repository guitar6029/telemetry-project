package com.joshsoll.telemetry.platform.metric.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.metric.entity.MetricDefinition;

public interface MetricDefinitionRepository
        extends JpaRepository<MetricDefinition, UUID> {

    boolean existsByDeviceTemplateAndIncomingFieldName(
            DeviceTemplate deviceTemplate,
            String incomingFieldName);
}
