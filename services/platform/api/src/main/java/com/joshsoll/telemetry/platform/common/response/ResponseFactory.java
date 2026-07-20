package com.joshsoll.telemetry.platform.common.response;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseFactory {

    private ResponseFactory() {
    }

    public static <T> ResponseEntity<ApiResponse<List<T>>> ok(
            List<T> data,
            String message) {
        ApiResponse<List<T>> response = new ApiResponse<>(data, message);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public static <T> ResponseEntity<PagedApiResponse<T>> ok(
            PagedApiResponse<T> response) {

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(
            T data,
            String message) {
        ApiResponse<T> response = new ApiResponse<T>(data, normalizeMessage(message));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(
            T data,
            String resource) {

        ApiResponse<T> response = new ApiResponse<T>(data, resource + " created successfully");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> updated(
            T data,
            String resource) {

        ApiResponse<T> response = new ApiResponse<T>(data, resource + " updated successfully");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    private static String normalizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        return message;
    }

}
