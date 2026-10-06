package com.company.employeemanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Integer status;
    private LocalDateTime timestamp;
    private String error;
    private Object errors;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, Integer status, LocalDateTime timestamp, String error, Object errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = timestamp;
        this.error = error;
        this.errors = errors;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public Object getErrors() { return errors; }
    public void setErrors(Object errors) { this.errors = errors; }

    public static <T> ApiResponseBuilder<T> builder() { return new ApiResponseBuilder<>(); }

    public static class ApiResponseBuilder<T> {
        private final ApiResponse<T> response = new ApiResponse<>();

        public ApiResponseBuilder<T> success(boolean success) { response.success = success; return this; }
        public ApiResponseBuilder<T> message(String message) { response.message = message; return this; }
        public ApiResponseBuilder<T> data(T data) { response.data = data; return this; }
        public ApiResponseBuilder<T> status(Integer status) { response.status = status; return this; }
        public ApiResponseBuilder<T> timestamp(LocalDateTime timestamp) { response.timestamp = timestamp; return this; }
        public ApiResponseBuilder<T> error(String error) { response.error = error; return this; }
        public ApiResponseBuilder<T> errors(Object errors) { response.errors = errors; return this; }

        public ApiResponse<T> build() { return response; }
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int status, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
