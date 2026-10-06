package com.company.employeemanagement.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int status;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, int status, LocalDateTime timestamp, Map<String, String> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }

    public static <T> ApiResponseBuilder<T> builder() { return new ApiResponseBuilder<>(); }

    public static class ApiResponseBuilder<T> {
        private final ApiResponse<T> r = new ApiResponse<>();

        public ApiResponseBuilder<T> success(boolean v) { r.success = v; return this; }
        public ApiResponseBuilder<T> message(String v) { r.message = v; return this; }
        public ApiResponseBuilder<T> data(T v) { r.data = v; return this; }
        public ApiResponseBuilder<T> status(int v) { r.status = v; return this; }
        public ApiResponseBuilder<T> timestamp(LocalDateTime v) { r.timestamp = v; return this; }
        public ApiResponseBuilder<T> errors(Map<String, String> v) { r.errors = v; return this; }

        public ApiResponse<T> build() { return r; }
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(201)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
