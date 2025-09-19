package com.example.codingbattle.dto;

import jakarta.validation.constraints.NotBlank;

public class SubmissionRequest {
    @NotBlank(message = "Room ID is required")
    private String roomId;

    @NotBlank(message = "Participant name is required")
    private String participantName;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Language is required")
    private String language;

    public SubmissionRequest() {}

    public SubmissionRequest(String roomId, String participantName, String code, String language) {
        this.roomId = roomId;
        this.participantName = participantName;
        this.code = code;
        this.language = language;
    }

    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

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
}
