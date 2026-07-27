package com.joshsoll.telemetry.platform.device.exception;

public class DuplicateDeviceSerialNumberException extends RuntimeException {
    public DuplicateDeviceSerialNumberException(String serialNumber) {
        super("A device with serial number "
                + serialNumber
                + " already exists in the organization.");
    }
}
