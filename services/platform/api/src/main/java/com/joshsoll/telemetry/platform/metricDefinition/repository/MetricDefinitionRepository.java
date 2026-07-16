package com.joshsoll.telemetry.platform.metricDefinition.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.metricDefinition.entity.MetricDefinition;

public interface MetricDefinitionRepository
                extends JpaRepository<MetricDefinition, UUID> {

        boolean existsByDeviceTemplateAndIncomingFieldName(
                        DeviceTemplate deviceTemplate,
                        String incomingFieldName);

        List<MetricDefinition> findAllByDeviceTemplateId(UUID deviceTemplateId);
}
