package com.example.codingbattle.dto;

import java.util.List;

public class CodeExecutionResult {
    private boolean success;
    private String output;
    private String error;
    private long executionTime;
    private int testCasesPassed;
    private int totalTestCases;
    private boolean allTestCasesPassed;
    private List<TestCaseResult> testCaseResults;

    public CodeExecutionResult() {}

    public CodeExecutionResult(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    public CodeExecutionResult(boolean success, String output, String error) {
        this.success = success;
        this.output = output;
        this.error = error;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
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

    public boolean isAllTestCasesPassed() {
        return allTestCasesPassed;
    }

    public void setAllTestCasesPassed(boolean allTestCasesPassed) {
        this.allTestCasesPassed = allTestCasesPassed;
    }

    public List<TestCaseResult> getTestCaseResults() {
        return testCaseResults;
    }

    public void setTestCaseResults(List<TestCaseResult> testCaseResults) {
        this.testCaseResults = testCaseResults;
    }

    public static class TestCaseResult {
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private boolean passed;
        private String error;

        public TestCaseResult() {}

        public TestCaseResult(String input, String expectedOutput, String actualOutput, boolean passed) {
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.actualOutput = actualOutput;
            this.passed = passed;
        }

        // Getters and Setters
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

        public String getActualOutput() {
            return actualOutput;
        }

        public void setActualOutput(String actualOutput) {
            this.actualOutput = actualOutput;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}