package com.example.codingbattle.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

public class TestCase {
    @Id
    private String id;

    @NotBlank(message = "Input is required")
    private String input;

    @NotBlank(message = "Expected output is required")
    private String expectedOutput;

    private String description;

    private boolean isHidden = false;

    public TestCase() {}

    public TestCase(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    public TestCase(String input, String expectedOutput, String description) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.description = description;
    }

    public TestCase(String input, String expectedOutput, String description, boolean isHidden) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.description = description;
        this.isHidden = isHidden;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
