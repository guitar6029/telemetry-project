package com.joshsoll.telemetry.platform.devicetemplate.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.service.AuthorizationService;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.devicetemplate.dto.CreateDeviceTemplateRequest;
import com.joshsoll.telemetry.platform.devicetemplate.dto.DeviceTemplateResponse;
import com.joshsoll.telemetry.platform.devicetemplate.dto.UpdateDeviceTemplateRequest;
import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DeviceTemplateNotFoundException;
import com.joshsoll.telemetry.platform.devicetemplate.exception.DuplicateDeviceTemplateNameException;
import com.joshsoll.telemetry.platform.devicetemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.metricdefinition.dto.MetricDefinitionResponse;
import com.joshsoll.telemetry.platform.metricdefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.metricdefinition.repository.MetricDefinitionRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

import jakarta.transaction.Transactional;

@Service
public class DeviceTemplateService {
        private final DeviceTemplateRepository deviceTemplateRepository;
        private final OrganizationRepository organizationRepository;
        private final AuthorizationService authorizationService;
        private final MetricDefinitionRepository metricDefinitionRepository;

        public DeviceTemplateService(
                        DeviceTemplateRepository deviceTemplateRepository,
                        OrganizationRepository organizationRepository,
                        AuthorizationService authorizationService,
                        MetricDefinitionRepository metricDefinitionRepository) {
                this.deviceTemplateRepository = deviceTemplateRepository;
                this.organizationRepository = organizationRepository;
                this.authorizationService = authorizationService;
                this.metricDefinitionRepository = metricDefinitionRepository;
        }

        @Transactional
        public DeviceTemplateResponse createDeviceTemplate(
                        User authenticatedUser,
                        UUID organizationId,
                        CreateDeviceTemplateRequest request) {

                Instant now = Instant.now();

                Organization organization = authorizationService.requireOrganizationAdmin(authenticatedUser,
                                organizationId);

                if (deviceTemplateRepository.existsByOrganizationAndName(organization, request.getName())) {
                        throw new DuplicateDeviceTemplateNameException(request.getName());
                }

                DeviceTemplate deviceTemplate = new DeviceTemplate(
                                request.getName(),
                                request.getDescription(),
                                organization,
                                false,
                                now,
                                now);

                DeviceTemplate savedDeviceTemplate = deviceTemplateRepository.save(deviceTemplate);

                List<MetricDefinition> metricDefinitions = request.getMetricDefinitions()
                                .stream()
                                .map(metricRequest -> new MetricDefinition(
                                                metricRequest.getName(),
                                                metricRequest.getDescription(),
                                                metricRequest.getIncomingFieldName(),
                                                metricRequest.getDataType(),
                                                metricRequest.getUnit(),
                                                savedDeviceTemplate,
                                                now,
                                                now))
                                .toList();

                List<MetricDefinition> savedMetricDefinitions = metricDefinitionRepository.saveAll(metricDefinitions);

                List<MetricDefinitionResponse> metricDefinitionResponses = savedMetricDefinitions.stream()
                                .map(this::toResponseMetricDefinition)
                                .toList();

                return new DeviceTemplateResponse(
                                savedDeviceTemplate.getId(),
                                savedDeviceTemplate.getName(),
                                savedDeviceTemplate.getDescription(),
                                savedDeviceTemplate.getOrganizationId(),
                                savedDeviceTemplate.isArchived(),
                                metricDefinitionResponses,
                                savedDeviceTemplate.getCreatedAt(),
                                savedDeviceTemplate.getUpdatedAt());
        }

        public DeviceTemplateResponse getDeviceTemplateById(UUID deviceTemplateId) {
                DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(deviceTemplateId)
                                .orElseThrow(() -> new DeviceTemplateNotFoundException(deviceTemplateId));
                return toResponse(deviceTemplate);
        }

        public PagedApiResponse<DeviceTemplateResponse> getDeviceTemplates(int page, int size) {
                Pageable pageable = PageRequest.of(page, size);

                Page<DeviceTemplate> deviceTemplates = deviceTemplateRepository.findAll(pageable);

                List<DeviceTemplateResponse> responses = new ArrayList<>();

                for (DeviceTemplate deviceTemplate : deviceTemplates) {
                        responses.add(toResponse(deviceTemplate));
                }

                return new PagedApiResponse<>(
                                responses,
                                "",
                                page,
                                size,
                                deviceTemplates.getTotalElements(),
                                deviceTemplates.getTotalPages());

        }

        public DeviceTemplateResponse updateDeviceTemplate(
                        UpdateDeviceTemplateRequest request,
                        UUID deviceTemplateId) {

                DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(deviceTemplateId)
                                .orElseThrow(() -> new DeviceTemplateNotFoundException(deviceTemplateId));

                Organization organization = organizationRepository.findById(request.getOrganizationId())
                                .orElseThrow(() -> new OrganizationNotFoundException(request.getOrganizationId()));

                boolean nameChanged = !deviceTemplate.getName().equals(request.getName());

                boolean organizationChanged = !deviceTemplate.getOrganizationId()
                                .equals(organization.getId());

                if ((nameChanged || organizationChanged)
                                && deviceTemplateRepository.existsByOrganizationAndName(
                                                organization,
                                                request.getName())) {

                        throw new DuplicateDeviceTemplateNameException(
                                        request.getName());
                }

                deviceTemplate.setName(request.getName());
                deviceTemplate.setDescription(request.getDescription());
                deviceTemplate.setOrganization(organization);

                DeviceTemplate savedDeviceTemplate = deviceTemplateRepository.save(deviceTemplate);

                return toResponse(savedDeviceTemplate);
        }

        public void deleteDeviceTemplate(UUID deviceTemplateId) {
                DeviceTemplate deviceTemplate = deviceTemplateRepository.findById(deviceTemplateId)
                                .orElseThrow(() -> new DeviceTemplateNotFoundException(deviceTemplateId));

                deviceTemplateRepository.delete(deviceTemplate);
        }

        private MetricDefinitionResponse toResponseMetricDefinition(MetricDefinition metricDefinition) {
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

        private DeviceTemplateResponse toResponse(DeviceTemplate deviceTemplate) {

                List<MetricDefinitionResponse> metricDefinitionResponses = metricDefinitionRepository
                                .findAllByDeviceTemplate(deviceTemplate)
                                .stream()
                                .map(this::toResponseMetricDefinition)
                                .toList();

                return new DeviceTemplateResponse(
                                deviceTemplate.getId(),
                                deviceTemplate.getName(),
                                deviceTemplate.getDescription(),
                                deviceTemplate.getOrganizationId(),
                                deviceTemplate.isArchived(),
                                metricDefinitionResponses,
                                deviceTemplate.getCreatedAt(),
                                deviceTemplate.getUpdatedAt());
        }
}
