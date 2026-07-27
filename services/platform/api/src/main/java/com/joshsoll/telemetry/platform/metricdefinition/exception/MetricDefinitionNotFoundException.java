package com.joshsoll.telemetry.platform.metricdefinition.exception;

import java.util.UUID;

public class MetricDefinitionNotFoundException extends RuntimeException {
    public MetricDefinitionNotFoundException(UUID metricDefinitionId) {
        super("Metric definition not found : " + metricDefinitionId);
    }
}
