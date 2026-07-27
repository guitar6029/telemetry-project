package com.joshsoll.telemetry.platform.metricdefinition.exception;

import com.joshsoll.telemetry.platform.metricdefinition.MetricDataType;

public class UnsupportedMetricDataTypeException extends RuntimeException {

    public UnsupportedMetricDataTypeException(MetricDataType dataType) {
        super("Unsupported metric data type: " + dataType + ".");
    }
}
