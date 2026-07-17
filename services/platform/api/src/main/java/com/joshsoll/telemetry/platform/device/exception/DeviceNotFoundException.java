package com.joshsoll.telemetry.platform.device.exception;

import java.util.UUID;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(UUID deviceId) {
        super("Device not found : " + deviceId);
    }
}
