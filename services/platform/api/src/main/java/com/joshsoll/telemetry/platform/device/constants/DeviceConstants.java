package com.joshsoll.telemetry.platform.device.constants;

public final class DeviceConstants {
    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 50;

    public static final int MODEL_MIN_LENGTH = 1;
    public static final int MODEL_MAX_LENGTH = 50;

    public static final int MANUFACTURER_MIN_LENGTH = 2;
    public static final int MANUFACTURER_MAX_LENGTH = 50;

    public static final int SERIAL_MIN_LENGTH = 2;
    public static final int SERIAL_MAX_LENGTH = 50;

    public static final int FIRMWARE_VERSION_MIN_LENGTH = 2;
    public static final int FIRMWARE_VERSION_MAX_LENGTH = 50;

    public static final int DEVICE_STATUS_MAX_LENGTH = 20;

    public static final String DOMAIN_NAME = "Device";

    private DeviceConstants() {
    }
}
