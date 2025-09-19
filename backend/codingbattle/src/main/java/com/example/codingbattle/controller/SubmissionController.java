package com.example.codingbattle.controller;

import com.example.codingbattle.dto.CodeExecutionResult;
import com.example.codingbattle.dto.SubmissionRequest;
import com.example.codingbattle.dto.SuccessResponse;
import com.example.codingbattle.model.Submission;
import com.example.codingbattle.service.SubmissionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class SubmissionController {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/submit")
    public ResponseEntity<SuccessResponse<CodeExecutionResult>> submitCode(@Valid @RequestBody SubmissionRequest request) {
        logger.info("Submitting code for room: {} by user: {}", request.getRoomId(), request.getParticipantName());

        CodeExecutionResult result = submissionService.submitCode(request);

        String message = result.isAllTestCasesPassed() ?
                "Code submitted successfully! All test cases passed." :
                String.format("Code submitted. %d out of %d test cases passed.",
                        result.getTestCasesPassed(), result.getTotalTestCases());

        SuccessResponse<CodeExecutionResult> response = new SuccessResponse<>(message, result);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<SuccessResponse<List<Submission>>> getSubmissionsByRoom(@PathVariable String roomId) {
        logger.info("Getting submissions for room: {}", roomId);

        List<Submission> submissions = submissionService.getSubmissionsByRoom(roomId);
        SuccessResponse<List<Submission>> response = new SuccessResponse<>("Submissions retrieved", submissions);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}/participant/{participantName}")
    public ResponseEntity<SuccessResponse<List<Submission>>> getSubmissionsByRoomAndParticipant(
            @PathVariable String roomId,
            @PathVariable String participantName) {
        logger.info("Getting submissions for room: {} by participant: {}", roomId, participantName);

        List<Submission> submissions = submissionService.getSubmissionsByRoomAndParticipant(roomId, participantName);
        SuccessResponse<List<Submission>> response = new SuccessResponse<>("Participant submissions retrieved", submissions);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}/accepted")
    public ResponseEntity<SuccessResponse<List<Submission>>> getAcceptedSubmissions(@PathVariable String roomId) {
        logger.info("Getting accepted submissions for room: {}", roomId);

        List<Submission> submissions = submissionService.getAcceptedSubmissions(roomId);
        SuccessResponse<List<Submission>> response = new SuccessResponse<>("Accepted submissions retrieved", submissions);

        return ResponseEntity.ok(response);
    }
}