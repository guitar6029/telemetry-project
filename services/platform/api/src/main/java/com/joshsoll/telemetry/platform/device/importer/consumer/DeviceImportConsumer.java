package com.joshsoll.telemetry.platform.device.importer.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;

import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportMessage;
import com.joshsoll.telemetry.platform.device.importer.service.DeviceImportProcessingService;

@Component
public class DeviceImportConsumer {

    private final DeviceImportProcessingService deviceImportProcessingService;

    public DeviceImportConsumer(
            DeviceImportProcessingService deviceImportProcessingService) {
        this.deviceImportProcessingService = deviceImportProcessingService;

    }

    @RabbitListener(queues = DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME)
    public void receiveMessage(DeviceImportMessage message) {
        deviceImportProcessingService.processImport(message);
    }
}
