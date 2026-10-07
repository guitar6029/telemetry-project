package com.joshsoll.telemetry.platform.device.importer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.entity.OutboxMessage;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
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
        publisher = new DeviceImportOutboxPublisher(outboxMessageRepository, rabbitTemplate, objectMapper);
        expectedMessage = new DeviceImportMessage(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                DeviceImportMode.SKIP_EXISTING,
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
        publisher.publishPendingMessages();

        ArgumentCaptor<DeviceImportMessage> messageCaptor = ArgumentCaptor.forClass(DeviceImportMessage.class);
        verify(rabbitTemplate).convertAndSend(eq(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME),
                messageCaptor.capture());
        assertEquals(expectedMessage.importId(), messageCaptor.getValue().importId());
        assertEquals(expectedMessage.organizationId(), messageCaptor.getValue().organizationId());
        assertEquals(expectedMessage.templateId(), messageCaptor.getValue().templateId());
        assertEquals(expectedMessage.hierarchyNodeId(), messageCaptor.getValue().hierarchyNodeId());
        assertEquals(expectedMessage.importMode(), messageCaptor.getValue().importMode());
        org.junit.jupiter.api.Assertions.assertArrayEquals(
                expectedMessage.csvData(), messageCaptor.getValue().csvData());
        verify(outboxMessageRepository).save(outboxMessage);
        assertNotNull(outboxMessage.getPublishedAt());
    }

    @Test
    void leavesMessageUnpublishedAndAvailableForRetryWhenRabbitPublishFails() {
        doThrow(new AmqpException("RabbitMQ unavailable"))
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(eq(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME), any(DeviceImportMessage.class));

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
