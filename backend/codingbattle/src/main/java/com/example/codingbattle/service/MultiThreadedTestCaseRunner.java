package com.example.codingbattle.service;

import com.example.codingbattle.dto.CodeExecutionRequest;
import com.example.codingbattle.dto.CodeExecutionResult;
import com.example.codingbattle.model.TestCase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MultiThreadedTestCaseRunner {

    private final CodeExecutionService codeExecutionService;

    public MultiThreadedTestCaseRunner(CodeExecutionService codeExecutionService) {
        this.codeExecutionService = codeExecutionService;
    }

    public CodeExecutionResult runTestCasesInParallel(String userCode, String language, List<TestCase> testCases) throws InterruptedException {

        List<CodeExecutionResult.TestCaseResult> testCaseResults = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger passedCount = new AtomicInteger(0);

        // Use virtual threads (Java 19+)
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();

            for (TestCase testCase : testCases) {
                futures.add(executor.submit(() -> {
                    CodeExecutionRequest request = new CodeExecutionRequest(userCode, language, testCase.getInput());
                    CodeExecutionResult execResult = codeExecutionService.executeCode(request);

                    boolean passed = false;
                    String actualOutput = "";
                    String error = null;

                    if (execResult.isSuccess()) {
                        actualOutput = execResult.getOutput().trim();
                        String expectedOutput = testCase.getExpectedOutput().trim();
                        passed = actualOutput.equals(expectedOutput);
                        if (passed) passedCount.incrementAndGet();
                    } else {
                        error = execResult.getError();
                    }

                    CodeExecutionResult.TestCaseResult tcResult = new CodeExecutionResult.TestCaseResult(
                            testCase.getInput(),
                            testCase.getExpectedOutput(),
                            actualOutput,
                            passed
                    );
                    tcResult.setError(error);
                    testCaseResults.add(tcResult);
                }));
            }

            // Wait for all virtual threads to finish
            for (Future<?> f : futures) {
                try {
                    f.get(15, TimeUnit.SECONDS); // optional per-test timeout
                } catch (ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }

        // Aggregate final result
        CodeExecutionResult finalResult = new CodeExecutionResult();
        finalResult.setSuccess(passedCount.get() > 0);
        finalResult.setTestCasesPassed(passedCount.get());
        finalResult.setTotalTestCases(testCases.size());
        finalResult.setAllTestCasesPassed(passedCount.get() == testCases.size());
        finalResult.setTestCaseResults(testCaseResults);
        finalResult.setOutput(passedCount.get() == testCases.size() ?
                "All test cases passed!" :
                String.format("%d out of %d test cases passed", passedCount.get(), testCases.size()));

        return finalResult;
    }
}
