package com.example.codingbattle.controller;

import com.example.codingbattle.dto.CodeExecutionRequest;
import com.example.codingbattle.dto.CodeExecutionResult;
import com.example.codingbattle.dto.SuccessResponse;
import com.example.codingbattle.service.SubmissionService;
import com.example.codingbattle.util.CodeLanguageUtils;
import com.example.codingbattle.validation.SupportedLanguage;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/code")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CodeExecutionController {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionController.class);

    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/run")
    public ResponseEntity<SuccessResponse<CodeExecutionResult>> runCode(@Valid @RequestBody CodeExecutionRequest request) {
        logger.info("Running code for language: {}", request.getLanguage());

        CodeExecutionResult result = submissionService.runCode(request);
        SuccessResponse<CodeExecutionResult> response = new SuccessResponse<>("Code executed", result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/languages")
    public ResponseEntity<SuccessResponse<List<String>>> getSupportedLanguages() {
        logger.info("Getting supported languages");

        List<String> languages = CodeLanguageUtils.getSupportedLanguages();
        SuccessResponse<List<String>> response = new SuccessResponse<>("Supported languages retrieved", languages);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/language/{language}/validate")
    public ResponseEntity<SuccessResponse<Map<String, Boolean>>> validateLanguage(@PathVariable String language) {
        logger.info("Validating language: {}", language);

        boolean isSupported = CodeLanguageUtils.isSupportedLanguage(language);
        Map<String, Boolean> result = Map.of("supported", isSupported);

        SuccessResponse<Map<String, Boolean>> response = new SuccessResponse<>(
                isSupported ? "Language is supported" : "Language is not supported",
                result
        );

        return ResponseEntity.ok(response);
    }
}