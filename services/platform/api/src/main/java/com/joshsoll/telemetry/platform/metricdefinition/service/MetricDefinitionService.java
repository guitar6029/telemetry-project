package com.joshsoll.telemetry.platform.metricdefinition.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateNotFoundException;
import com.joshsoll.telemetry.platform.devicetemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.metricdefinition.dto.CreateMetricDefinitionRequest;
import com.joshsoll.telemetry.platform.metricdefinition.dto.MetricDefinitionResponse;
import com.joshsoll.telemetry.platform.metricdefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.metricdefinition.exception.DuplicateMetricDefinitionFieldException;
import com.joshsoll.telemetry.platform.metricdefinition.exception.MetricDefinitionNotFoundException;
import com.joshsoll.telemetry.platform.metricdefinition.repository.MetricDefinitionRepository;

@Service
public class MetricDefinitionService {
    private final MetricDefinitionRepository metricDefinitionRepository;
    private final DeviceTemplateRepository deviceTemplateRepository;

    public MetricDefinitionService(MetricDefinitionRepository metricDefinitionRepository,
            DeviceTemplateRepository deviceTemplateRepository) {
        this.metricDefinitionRepository = metricDefinitionRepository;
        this.deviceTemplateRepository = deviceTemplateRepository;
    }

    public MetricDefinitionResponse createMetricDefinition(CreateMetricDefinitionRequest request) {

        Instant now = Instant.now();

        // find device template
        DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(request.getDeviceTemplateId())
                .orElseThrow(() -> new DeviceTemplateNotFoundException(request.getDeviceTemplateId()));

        // validate given device template does not already have the metric
        if (metricDefinitionRepository.existsByDeviceTemplateAndIncomingFieldName(deviceTemplate,
                request.getIncomingFieldName())) {
            throw new DuplicateMetricDefinitionFieldException(request.getIncomingFieldName());
        }

        MetricDefinition metricDefinition = new MetricDefinition(
                request.getName(),
                request.getDescription(),
                request.getIncomingFieldName(),
                request.getDataType(),
                request.getUnit(),
                deviceTemplate,
                now,
                now);

        MetricDefinition savedMetricDefinition = metricDefinitionRepository.save(metricDefinition);

        return toResponse(savedMetricDefinition);
    }

    public MetricDefinitionResponse getMetricDefinitionById(UUID metricDefinitionId) {
        // check if metric exists with the given id
        MetricDefinition metricDefinition = metricDefinitionRepository.findById(metricDefinitionId)
                .orElseThrow(() -> new MetricDefinitionNotFoundException(metricDefinitionId));

        return toResponse(metricDefinition);
    }

    public PagedApiResponse<MetricDefinitionResponse> getMetricDefinitions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<MetricDefinition> metricDefinitions = metricDefinitionRepository.findAll(pageable);

        List<MetricDefinitionResponse> responses = new ArrayList<>();

        for (MetricDefinition metricDefinition : metricDefinitions) {
            responses.add(toResponse(metricDefinition));
        }

        return new PagedApiResponse<>(
                responses,
                "",
                page,
                size,
                metricDefinitions.getTotalElements(),
                metricDefinitions.getTotalPages());

    }

    private MetricDefinitionResponse toResponse(MetricDefinition metricDefinition) {
        return new MetricDefinitionResponse(
                metricDefinition.getId(),
                metricDefinition.getName(),
                metricDefinition.getDescription(),
                metricDefinition.getIncomingFieldName(),
                metricDefinition.getDataType(),
                metricDefinition.getUnit(),
                metricDefinition.getDeviceTemplateId(),
                metricDefinition.getCreatedAt(),
                metricDefinition.getUpdatedAt());
    }
}
