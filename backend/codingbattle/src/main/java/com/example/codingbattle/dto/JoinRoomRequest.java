package com.example.codingbattle.dto;

import jakarta.validation.constraints.NotBlank;

public class JoinRoomRequest {
    @NotBlank(message = "Room ID is required")
    private String roomId;

    @NotBlank(message = "Participant name is required")
    private String participantName;

    public JoinRoomRequest() {}

    public JoinRoomRequest(String roomId, String participantName) {
        this.roomId = roomId;
        this.participantName = participantName;
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
}
