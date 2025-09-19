package com.example.codingbattle.service;

import com.example.codingbattle.dto.CodeExecutionRequest;
import com.example.codingbattle.dto.CodeExecutionResult;
import com.example.codingbattle.dto.SubmissionRequest;
import com.example.codingbattle.model.Room;
import com.example.codingbattle.model.Submission;
import com.example.codingbattle.model.TestCase;
import com.example.codingbattle.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private CodeExecutionService codeExecutionService;

    @Autowired
    private MultiThreadedTestCaseRunner multiThreadedTestCaseRunner;

    public CodeExecutionResult submitCode(SubmissionRequest request) {
        logger.info("Processing submission for room: {} by user: {}", request.getRoomId(), request.getParticipantName());

        // Get room details
        Room room = roomService.getRoomByRoomId(request.getRoomId());

        // Execute code against all test cases
        CodeExecutionResult result = executeAgainstTestCases(request, room.getTestCases());

        // Save submission
        Submission submission = createSubmission(request, result);
        submissionRepository.save(submission);

        logger.info("Submission saved with result: {} test cases passed out of {}",
                result.getTestCasesPassed(), result.getTotalTestCases());

        return result;
    }

    public CodeExecutionResult runCode(CodeExecutionRequest request) {
        logger.info("Running code for language: {}", request.getLanguage());

        if (request.getRoomId() != null && !request.getRoomId().isEmpty()) {
            // If room ID is provided, run against sample test cases
            Room room = roomService.getRoomByRoomId(request.getRoomId());
            List<TestCase> sampleTestCases = getSampleTestCases(room.getTestCases());

            if (!sampleTestCases.isEmpty()) {
                SubmissionRequest submissionRequest = new SubmissionRequest(
                        request.getRoomId(), "temp", request.getCode(), request.getLanguage()
                );
                return executeAgainstTestCases(submissionRequest, sampleTestCases);
            }
        }

        // Run with provided input
        return codeExecutionService.executeCode(request);
    }

    public List<Submission> getSubmissionsByRoom(String roomId) {
        return submissionRepository.findByRoomId(roomId);
    }

    public List<Submission> getSubmissionsByRoomAndParticipant(String roomId, String participantName) {
        return submissionRepository.findByRoomIdAndParticipantName(roomId, participantName);
    }

    public List<Submission> getAcceptedSubmissions(String roomId) {
        return submissionRepository.findAcceptedSubmissionsByRoomId(roomId);
    }

    private CodeExecutionResult executeAgainstTestCases(SubmissionRequest request, List<TestCase> testCases) {
        try {
            return multiThreadedTestCaseRunner.runTestCasesInParallel(request.getCode(), request.getLanguage(), testCases);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private Submission createSubmission(SubmissionRequest request, CodeExecutionResult result) {
        Submission submission = new Submission();
        submission.setRoomId(request.getRoomId());
        submission.setParticipantName(request.getParticipantName());
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setAccepted(result.isAllTestCasesPassed());
        submission.setTestCasesPassed(result.getTestCasesPassed());
        submission.setTotalTestCases(result.getTotalTestCases());
        submission.setResult(result.getOutput());
        submission.setExecutionTime(result.getExecutionTime());

        if (!result.isSuccess() && result.getError() != null) {
            submission.setErrorMessage(result.getError());
        }

        return submission;
    }

    private List<TestCase> getSampleTestCases(List<TestCase> allTestCases) {
        // Return non-hidden test cases for running/testing
        return allTestCases.stream()
                .filter(tc -> !tc.isHidden())
                .toList();
    }
}