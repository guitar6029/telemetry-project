package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.entity.OutboxMessage;
import com.joshsoll.telemetry.platform.device.importer.repository.OutboxMessageRepository;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DeviceImportOutboxPublisherTest {

    @Mock
    private OutboxMessageRepository outboxMessageRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DeviceImportOutboxPublisher publisher;
    private DeviceImportMessage expectedMessage;
    private OutboxMessage outboxMessage;

    @BeforeEach
    void setUp() throws Exception {
        publisher = new DeviceImportOutboxPublisher(outboxMessageRepository, rabbitTemplate, objectMapper, 30);
        expectedMessage = new DeviceImportMessage(
                UUID.randomUUID(),
                "name,manufacturer,model,serialnumber,firmwareversion,status\n".getBytes(StandardCharsets.UTF_8));
        outboxMessage = new OutboxMessage(
                DeviceImportConstants.DEVICE_IMPORT_OUTBOX_EVENT_TYPE,
                expectedMessage.importId(),
                objectMapper.writeValueAsString(expectedMessage),
                Instant.now());
        when(outboxMessageRepository.findTop10ByPublishedAtIsNullOrderByCreatedAtAsc())
                .thenReturn(List.of(outboxMessage));
    }

    @Test
    void publishesExistingMessageAndMarksOutboxRecordPublished() {
        doAnswer(invocation -> {
            CorrelationData correlationData = invocation.getArgument(2);
            correlationData.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbitTemplate).convertAndSend(
                eq(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME),
                any(DeviceImportMessage.class), any(CorrelationData.class));

        publisher.publishPendingMessages();

        ArgumentCaptor<DeviceImportMessage> messageCaptor = ArgumentCaptor.forClass(DeviceImportMessage.class);
        ArgumentCaptor<CorrelationData> correlationCaptor = ArgumentCaptor.forClass(CorrelationData.class);
        verify(rabbitTemplate).convertAndSend(eq(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME),
                messageCaptor.capture(), correlationCaptor.capture());
        assertEquals(expectedMessage.importId(), messageCaptor.getValue().importId());
        org.junit.jupiter.api.Assertions.assertArrayEquals(
                expectedMessage.csvData(), messageCaptor.getValue().csvData());
        assertEquals(outboxMessage.getId().toString(), correlationCaptor.getValue().getId());
        verify(outboxMessageRepository).save(outboxMessage);
        assertNotNull(outboxMessage.getPublishedAt());
    }

    @Test
    void leavesMessageUnpublishedAndAvailableForRetryWhenBrokerNacks() {
        doAnswer(invocation -> {
            CorrelationData correlationData = invocation.getArgument(2);
            correlationData.getFuture().complete(new CorrelationData.Confirm(false, "broker rejected"));
            return null;
        }).when(rabbitTemplate).convertAndSend(
                eq(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME),
                any(DeviceImportMessage.class), any(CorrelationData.class));

        assertThrows(IllegalStateException.class, () -> publisher.publishPendingMessages());

        assertNull(outboxMessage.getPublishedAt());
        verify(outboxMessageRepository, never()).save(any(OutboxMessage.class));
        verify(outboxMessageRepository).findTop10ByPublishedAtIsNullOrderByCreatedAtAsc();
    }

    @Test
    void leavesMessageUnpublishedAndAvailableForRetryWhenConfirmationTimesOut() {
        assertThrows(IllegalStateException.class, () -> publisher.publishPendingMessages());

        assertNull(outboxMessage.getPublishedAt());
        verify(outboxMessageRepository, never()).save(any(OutboxMessage.class));
        verify(outboxMessageRepository).findTop10ByPublishedAtIsNullOrderByCreatedAtAsc();
    }

    @Test
    void leavesMessageUnpublishedAndAvailableForRetryWhenRabbitPublishFails() {
        AtomicInteger publishAttempts = new AtomicInteger();
        doAnswer(invocation -> {
            if (publishAttempts.getAndIncrement() == 0) {
                throw new AmqpException("RabbitMQ unavailable");
            }
            CorrelationData correlationData = invocation.getArgument(2);
            correlationData.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbitTemplate).convertAndSend(eq(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME),
                any(DeviceImportMessage.class), any(CorrelationData.class));

        assertThrows(AmqpException.class, () -> publisher.publishPendingMessages());

        assertNull(outboxMessage.getPublishedAt());
        verify(outboxMessageRepository, never()).save(any(OutboxMessage.class));
        verify(outboxMessageRepository).findTop10ByPublishedAtIsNullOrderByCreatedAtAsc();

        publisher.publishPendingMessages();

        assertNotNull(outboxMessage.getPublishedAt());
        verify(outboxMessageRepository).save(outboxMessage);
        verify(outboxMessageRepository, org.mockito.Mockito.times(2))
                .findTop10ByPublishedAtIsNullOrderByCreatedAtAsc();
    }
}
