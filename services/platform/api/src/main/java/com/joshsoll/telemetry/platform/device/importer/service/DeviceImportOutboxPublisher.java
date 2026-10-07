package com.joshsoll.telemetry.platform.device.importer.service;

import java.time.Instant;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.entity.OutboxMessage;
import com.joshsoll.telemetry.platform.device.importer.repository.OutboxMessageRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class DeviceImportOutboxPublisher {

    private final OutboxMessageRepository outboxMessageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public DeviceImportOutboxPublisher(
            OutboxMessageRepository outboxMessageRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {
        this.outboxMessageRepository = outboxMessageRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${device-import.outbox.poll-interval-ms:1000}")
    public void publishPendingMessages() {
        for (OutboxMessage outboxMessage : outboxMessageRepository
                .findTop10ByPublishedAtIsNullOrderByCreatedAtAsc()) {
            if (!DeviceImportConstants.DEVICE_IMPORT_OUTBOX_EVENT_TYPE.equals(outboxMessage.getEventType())) {
                throw new IllegalStateException("Unsupported outbox event type: " + outboxMessage.getEventType());
            }

            DeviceImportMessage message;
            try {
                message = objectMapper.readValue(outboxMessage.getPayload(), DeviceImportMessage.class);
            } catch (JacksonException exception) {
                throw new IllegalStateException("Unable to deserialize device import outbox message", exception);
            }

            rabbitTemplate.convertAndSend(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME, message);
            outboxMessage.markPublished(Instant.now());
            outboxMessageRepository.save(outboxMessage);
        }
    }
}
