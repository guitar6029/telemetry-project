package com.joshsoll.telemetry.platform.metricdefinition.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.metricdefinition.entity.MetricDefinition;

public interface MetricDefinitionRepository
                extends JpaRepository<MetricDefinition, UUID> {

        boolean existsByDeviceTemplateAndIncomingFieldName(
                        DeviceTemplate deviceTemplate,
                        String incomingFieldName);

        List<MetricDefinition> findAllByDeviceTemplate(DeviceTemplate deviceTemplate);

        Optional<MetricDefinition> findByDeviceTemplateAndIncomingFieldName(
                        DeviceTemplate deviceTemplate,
                        String incomingFieldName);

        List<MetricDefinition> findAllByDeviceTemplate_Id(UUID deviceTemplateId);
}
