package com.joshsoll.telemetry.platform.device.importer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.joshsoll.telemetry.platform.device.importer.constants.DeviceImportConstants;

@Configuration
public class DeviceImportRabbitConfig {

    @Bean
    public Queue deviceImportQueue() {
        return new Queue(DeviceImportConstants.DEVICE_IMPORT_QUEUE_NAME, true);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
