package com.joshsoll.telemetry.platform.device.importer.service;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.connection.CorrelationData;
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
    private final long confirmationTimeoutMillis;

    public DeviceImportOutboxPublisher(
            OutboxMessageRepository outboxMessageRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            @Value("${device-import.outbox.confirm-timeout-ms:5000}") long confirmationTimeoutMillis) {
        this.outboxMessageRepository = outboxMessageRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.confirmationTimeoutMillis = confirmationTimeoutMillis;
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

            CorrelationData correlationData = new CorrelationData(outboxMessage.getId().toString());
            rabbitTemplate.convertAndSend(
                    DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME, message, correlationData);
            try {
                CorrelationData.Confirm confirm = correlationData.getFuture()
                        .get(confirmationTimeoutMillis, TimeUnit.MILLISECONDS);
                if (!confirm.ack()) {
                    throw new IllegalStateException("RabbitMQ negatively acknowledged device import message: "
                            + confirm.reason());
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted waiting for RabbitMQ publisher confirmation", exception);
            } catch (ExecutionException | TimeoutException exception) {
                throw new IllegalStateException("Unable to confirm device import message publication", exception);
            }

            outboxMessage.markPublished(Instant.now());
            outboxMessageRepository.save(outboxMessage);
        }
    }
}
