package com.example.codingbattle.dto;

import java.time.LocalDateTime;

// SuccessResponse.java
public class SuccessResponse<T> {
    private boolean success = true;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public SuccessResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponse(String message, T data) {
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponse(T data) {
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
