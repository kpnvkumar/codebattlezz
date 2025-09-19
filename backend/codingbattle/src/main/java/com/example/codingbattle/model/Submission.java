package com.example.codingbattle.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.LocalDateTime;

@Document(collection = "submissions")
@CompoundIndex(def = "{'roomId': 1, 'participantName': 1, 'createdAt': -1}")
public class Submission {
    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Room ID is required")
    private String roomId;

    @NotBlank(message = "Participant name is required")
    private String participantName;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Language is required")
    private String language;

    private boolean isAccepted = false;

    private int testCasesPassed = 0;

    private int totalTestCases = 0;

    private String result;

    private String errorMessage;

    private long executionTime; // in milliseconds

    @CreatedDate
    private LocalDateTime createdAt;

    public Submission() {}

    public Submission(String roomId, String participantName, String code, String language) {
        this.roomId = roomId;
        this.participantName = participantName;
        this.code = code;
        this.language = language;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public int getTestCasesPassed() {
        return testCasesPassed;
    }

    public void setTestCasesPassed(int testCasesPassed) {
        this.testCasesPassed = testCasesPassed;
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
