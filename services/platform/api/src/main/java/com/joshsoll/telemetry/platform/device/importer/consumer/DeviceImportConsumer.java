package com.joshsoll.telemetry.platform.device.importer.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;

@Component
public class DeviceImportConsumer {
    @RabbitListener(queues = DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME)
    public void receiveMessage(String demo) {
        System.out.println("Received message : " + demo);
    }
}
