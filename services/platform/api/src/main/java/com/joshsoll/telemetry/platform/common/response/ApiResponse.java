package com.joshsoll.telemetry.platform.common.response;

public class ApiResponse<T> {

    private final T data;
    private final String message;

    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
