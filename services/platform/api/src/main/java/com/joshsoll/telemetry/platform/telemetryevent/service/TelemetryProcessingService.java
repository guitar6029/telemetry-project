package com.joshsoll.telemetry.platform.telemetryevent.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.metricdefinition.MetricDataType;
import com.joshsoll.telemetry.platform.metricdefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.metricdefinition.exception.UnsupportedMetricDataTypeException;
import com.joshsoll.telemetry.platform.metricdefinition.repository.MetricDefinitionRepository;
import com.joshsoll.telemetry.platform.metricvalue.entity.MetricValue;
import com.joshsoll.telemetry.platform.metricvalue.repository.MetricValueRepository;
import com.joshsoll.telemetry.platform.telemetryevent.entity.TelemetryEvent;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class TelemetryProcessingService {

    private final ObjectMapper objectMapper;
    private final MetricDefinitionRepository metricDefinitionRepository;
    private final MetricValueRepository metricValueRepository;

    public TelemetryProcessingService(

            ObjectMapper objectMapper,

            MetricDefinitionRepository metricDefinitionRepository,

            MetricValueRepository metricValueRepository

    ) {
        this.objectMapper = objectMapper;
        this.metricDefinitionRepository = metricDefinitionRepository;
        this.metricValueRepository = metricValueRepository;
    }

    public void processTelemetryEvent(TelemetryEvent telemetryEvent) {

        String rawPayload = telemetryEvent.getRawPayload();

        Map<String, Object> payload = objectMapper.readValue(rawPayload, new TypeReference<Map<String, Object>>() {

        });

        // Load MetricDefinitions
        List<MetricDefinition> metricDefinitions = metricDefinitionRepository
                .findAllByDeviceTemplate(telemetryEvent.getDevice().getDeviceTemplate());

        Map<String, MetricDefinition> definitionLookup = metricDefinitions.stream()
                .collect(Collectors.toMap(
                        MetricDefinition::getIncomingFieldName,
                        Function.identity()));

        // iterate over the metricDefinitions
        // compare the definition with the payload's key and value types
        // if there is no key , then we log it , but skip it
        // everything gets logged no matter what so record keeping
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String incomingField = entry.getKey();

            // check if incomingField matches with the
            // already defined metric definition
            MetricDefinition definition = definitionLookup.get(incomingField);

            // if not found , log it , skip it
            if (definition == null) {
                // log it then skip
                continue;
            }

            Object value = entry.getValue();
            // check the value meets the type
            MetricDataType expectedDataType = definition.getDataType();

            // now compare value type and dataType
            // if does not match also log it then continue
            // if all is good , log it as well and save the payload with whatever is legit
            boolean validValueType = isValidType(expectedDataType, value);
            if (!validValueType) {
                // log it
                continue;
            }

            MetricValue metricValue = createMetricValue(telemetryEvent, definition, value);

            metricValueRepository.save(metricValue);
        }

    }

    private boolean isValidType(
            MetricDataType expectedType,
            Object value) {

        switch (expectedType) {

            case NUMBER:
                return value instanceof Number;

            case BOOLEAN:
                return value instanceof Boolean;

            case STRING:
                return value instanceof String;

            default:
                return false;
        }
    }

    private MetricValue createMetricValue(
            TelemetryEvent telemetryEvent,
            MetricDefinition definition,
            Object value) {

        Instant now = Instant.now();

        switch (definition.getDataType()) {

            case NUMBER:

                Number number = (Number) value;

                BigDecimal numberValue = new BigDecimal(number.toString());

                return new MetricValue(
                        telemetryEvent,
                        definition,
                        numberValue,
                        null,
                        null,
                        now);

            case BOOLEAN:
                return new MetricValue(
                        telemetryEvent,
                        definition,
                        null,
                        (Boolean) value,
                        null,
                        now);

            case STRING:
                return new MetricValue(
                        telemetryEvent,
                        definition,
                        null,
                        null,
                        (String) value,
                        now);

            default:
                throw new UnsupportedMetricDataTypeException(definition.getDataType());
        }
    }
}
