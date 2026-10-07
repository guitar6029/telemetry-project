package com.joshsoll.telemetry.platform.device.importer.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportContext;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.entity.OutboxMessage;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportRepository;
import com.joshsoll.telemetry.platform.device.importer.repository.OutboxMessageRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class DeviceImportSubmissionService {

    private final DeviceImportRepository deviceImportRepository;
    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;

    public DeviceImportSubmissionService(
            DeviceImportRepository deviceImportRepository,
            OutboxMessageRepository outboxMessageRepository,
            ObjectMapper objectMapper) {
        this.deviceImportRepository = deviceImportRepository;
        this.outboxMessageRepository = outboxMessageRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public DeviceImport createImportAndOutboxMessage(
            DeviceImportContext context,
            String filename,
            DeviceImportMode importMode,
            byte[] csvData) {
        Instant submittedAt = Instant.now();
        DeviceImport deviceImport = new DeviceImport(
                context.organization(),
                context.deviceTemplate(),
                context.hierarchyNode(),
                filename,
                importMode,
                submittedAt);
        DeviceImportMessage message = new DeviceImportMessage(
                deviceImport.getId(),
                context.organization().getId(),
                context.deviceTemplate().getId(),
                context.hierarchyNode().getId(),
                importMode,
                csvData);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(message);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Unable to serialize device import message", exception);
        }

        deviceImportRepository.save(deviceImport);
        outboxMessageRepository.save(new OutboxMessage(
                DeviceImportConstants.DEVICE_IMPORT_OUTBOX_EVENT_TYPE,
                deviceImport.getId(),
                payload,
                submittedAt));

        return deviceImport;
    }
}
