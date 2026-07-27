package com.joshsoll.telemetry.platform.metricdefinition.exception;

public class DuplicateMetricDefinitionFieldException extends RuntimeException {

    public DuplicateMetricDefinitionFieldException(String incomingFieldName) {
        super("Metric definition already exists for incoming field "
                + incomingFieldName + ".");
    }
}
