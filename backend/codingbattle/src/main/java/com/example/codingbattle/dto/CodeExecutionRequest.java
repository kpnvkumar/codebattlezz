package com.example.codingbattle.dto;

import jakarta.validation.constraints.NotBlank;

public class CodeExecutionRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Language is required")
    private String language;

    private String input;

    private String roomId;

    public CodeExecutionRequest() {}

    public CodeExecutionRequest(String code, String language) {
        this.code = code;
        this.language = language;
    }

    public CodeExecutionRequest(String code, String language, String input) {
        this.code = code;
        this.language = language;
        this.input = input;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
