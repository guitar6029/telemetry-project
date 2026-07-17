package com.joshsoll.telemetry.platform.metricDefinition.exception;

import java.util.UUID;

public class MetricDefinitionNotFoundException extends RuntimeException {
    public MetricDefinitionNotFoundException(UUID metricDefinitionId) {
        super("Metric definition not found : " + metricDefinitionId);
    }
}
