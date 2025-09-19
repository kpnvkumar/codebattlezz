package com.example.codingbattle.dto;
import java.time.LocalDateTime;
import java.util.*;
// ErrorResponse.java
public class ErrorResponse {
    private boolean success = false;
    private String message;
    private String error;
    private int status;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, String error, int status, String path) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.path = path;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
