package com.example.codingbattle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.example.codingbattle.model.TestCase;

import java.util.List;

public class CreateRoomRequest {
    @NotBlank(message = "Question is required")
    private String question;

    @NotNull(message = "Test cases are required")
    @Size(min = 1, message = "At least one test case is required")
    private List<TestCase> testCases;

    private String createdBy;

    private String difficulty;

    public CreateRoomRequest() {}

    public CreateRoomRequest(String question, List<TestCase> testCases) {
        this.question = question;
        this.testCases = testCases;
    }

    public CreateRoomRequest(String question, List<TestCase> testCases, String createdBy) {
        this.question = question;
        this.testCases = testCases;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
