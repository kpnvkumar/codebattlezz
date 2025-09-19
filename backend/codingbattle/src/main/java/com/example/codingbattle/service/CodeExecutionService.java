//package com.example.codingbattle.service;
//import com.example.codingbattle.dto.CodeExecutionRequest;
//import com.example.codingbattle.dto.CodeExecutionResult;
//import com.example.codingbattle.util.CodeLanguageUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.concurrent.TimeUnit;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class CodeExecutionService {
//
//    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionService.class);
//    private static final int TIMEOUT_SECONDS = 10;
//    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "codingbattle";
//
//    public CodeExecutionService() {
//        // Create temp directory if it doesn't exist
//        try {
//            Path tempPath = Paths.get(TEMP_DIR);
//            if (!Files.exists(tempPath)) {
//                Files.createDirectories(tempPath);
//            }
//        } catch (IOException e) {
//            logger.error("Failed to create temp directory: {}", e.getMessage());
//        }
//    }
//
//    public CodeExecutionResult executeCode(CodeExecutionRequest request) {
//        logger.info("Executing code for language: {}", request.getLanguage());
//
//        if (!CodeLanguageUtils.isSupportedLanguage(request.getLanguage())) {
//            return new CodeExecutionResult(false, null, "Unsupported language: " + request.getLanguage());
//        }
//
//        String sessionId = generateSessionId();
//        Path workingDir = Paths.get(TEMP_DIR, sessionId);
//
//        try {
//            Files.createDirectories(workingDir);
//            return executeCodeInDirectory(request, workingDir);
//        } catch (Exception e) {
//            logger.error("Error executing code: {}", e.getMessage(), e);
//            return new CodeExecutionResult(false, null, "Execution error: " + e.getMessage());
//        } finally {
//            // Cleanup
//            try {
//                deleteDirectory(workingDir);
//            } catch (IOException e) {
//                logger.warn("Failed to cleanup directory: {}", e.getMessage());
//            }
//        }
//    }
//
//    private CodeExecutionResult executeCodeInDirectory(CodeExecutionRequest request, Path workingDir) throws IOException, InterruptedException {
//        String language = request.getLanguage().toLowerCase();
//        String code = request.getCode();
//        String input = request.getInput() != null ? request.getInput() : "";
//
//        // Generate filename
//        String filename = CodeLanguageUtils.generateFileName(language, "Solution");
//        Path codePath = workingDir.resolve(filename);
//
//        // Write code to file
//        Files.write(codePath, code.getBytes());
//
//        long startTime = System.currentTimeMillis();
//
//        // Compile if necessary
//        if (CodeLanguageUtils.needsCompilation(language)) {
//            CodeExecutionResult compileResult = compileCode(language, codePath, workingDir);
//            if (!compileResult.isSuccess()) {
//                return compileResult;
//            }
//        }
//
//        // Execute code
//        CodeExecutionResult result = runCode(language, codePath, workingDir, input);
//
//        long executionTime = System.currentTimeMillis() - startTime;
//        result.setExecutionTime(executionTime);
//
//        return result;
//    }
//
//    private CodeExecutionResult compileCode(String language, Path codePath, Path workingDir) throws IOException, InterruptedException {
//        String compileCommand = CodeLanguageUtils.getCompileCommand(language);
//        if (compileCommand == null) {
//            return new CodeExecutionResult(true, null);
//        }
//
//        String filename = codePath.getFileName().toString();
//        String output = getOutputFilename(language, filename);
//
//        compileCommand = compileCommand
//                .replace("{filename}", filename)
//                .replace("{output}", output);
//
//        logger.info("=== COMPILATION DEBUG ===");
//        logger.info("Language: {}", language);
//        logger.info("Working directory: {}", workingDir.toAbsolutePath());
//        logger.info("Source file: {}", codePath.toAbsolutePath());
//        logger.info("Expected output: {}", output);
//        logger.info("Full compile command: {}", compileCommand);
//
//        // Check if source file exists
//        if (!Files.exists(codePath)) {
//            logger.error("Source file does not exist: {}", codePath);
//            return new CodeExecutionResult(false, null, "Source file not found: " + codePath);
//        }
//
//        // For Java, check if javac is available
//        if ("java".equals(language)) {
//            try {
//                ProcessBuilder testPb = new ProcessBuilder("javac", "-version");
//                Process testProcess = testPb.start();
//                testProcess.waitFor(5, TimeUnit.SECONDS);
//                // Note: javac -version outputs to stderr, not stdout, so we check differently
//            } catch (Exception e) {
//                logger.error("Error checking javac availability: {}", e.getMessage());
//                return new CodeExecutionResult(false, null, "javac compiler not found: " + e.getMessage());
//            }
//        } else if ("cpp".equals(language) || "c".equals(language)) {
//            // Check if g++ or gcc is available
//            String compiler = "cpp".equals(language) ? "g++" : "gcc";
//            try {
//                ProcessBuilder testPb = new ProcessBuilder(compiler, "--version");
//                Process testProcess = testPb.start();
//                testProcess.waitFor(5, TimeUnit.SECONDS);
//                if (testProcess.exitValue() != 0) {
//                    logger.error("{} is not available or not working properly", compiler);
//                    return new CodeExecutionResult(false, null, compiler + " compiler not found or not working");
//                }
//            } catch (Exception e) {
//                logger.error("Error checking {} availability: {}", compiler, e.getMessage());
//                return new CodeExecutionResult(false, null, compiler + " compiler check failed: " + e.getMessage());
//            }
//        }
//
//        // Split command properly
//        List<String> commandList = new ArrayList<>();
//        String[] parts = compileCommand.split("\\s+");
//        for (String part : parts) {
//            commandList.add(part);
//        }
//
//        logger.info("Command list: {}", commandList);
//
//        ProcessBuilder pb = new ProcessBuilder(commandList);
//        pb.directory(workingDir.toFile());
//        pb.redirectErrorStream(false); // Separate stdout and stderr
//
//        Process process = pb.start();
//        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
//
//        String stdout = readProcessOutput(process.getInputStream());
//        String stderr = readProcessOutput(process.getErrorStream());
//
//        logger.info("Compilation finished: {}", finished);
//        logger.info("Exit code: {}", process.exitValue());
//        logger.info("Compilation stdout: {}", stdout);
//        logger.info("Compilation stderr: {}", stderr);
//
//        if (!finished) {
//            process.destroyForcibly();
//            return new CodeExecutionResult(false, null, "Compilation timeout");
//        }
//
//        if (process.exitValue() != 0) {
//            String error = !stderr.isEmpty() ? stderr : stdout;
//            logger.error("Compilation failed with error: {}", error);
//            return new CodeExecutionResult(false, null, "Compilation error: " + error);
//        }
//
//        // Verify that the compiled output was created
//        if ("java".equals(language)) {
//            // For Java, check for .class file
//            Path classFile = workingDir.resolve(output + ".class");
//            logger.info("Checking for Java class file at: {}", classFile.toAbsolutePath());
//
//            if (!Files.exists(classFile)) {
//                // List all files in the directory to see what was created
//                try {
//                    logger.info("Files in working directory:");
//                    Files.list(workingDir).forEach(file -> logger.info("  - {}", file.getFileName()));
//                } catch (IOException e) {
//                    logger.warn("Could not list directory contents: {}", e.getMessage());
//                }
//                return new CodeExecutionResult(false, null, "Compilation succeeded but class file not found: " + output + ".class");
//            }
//
//            logger.info("Java compilation successful, class file created: {}", classFile.toAbsolutePath());
//        } else {
//            // For C/C++, check for executable
//            Path executablePath = workingDir.resolve(output);
//            logger.info("Checking for executable at: {}", executablePath.toAbsolutePath());
//
//            if (!Files.exists(executablePath)) {
//                // List all files in the directory to see what was created
//                try {
//                    logger.info("Files in working directory: 1");
//                    Files.list(workingDir).forEach(file -> logger.info("  hi- {}", file.getFileName()));
//                } catch (IOException e) {
//                    logger.warn("Could not list directory contents: {}", e.getMessage());
//                }
//                return new CodeExecutionResult(false, null, "Compilation succeeded but executable not found: " + output);
//            }
//
//            logger.info("Compilation successful, executable created: {}", executablePath.toAbsolutePath());
//            logger.info("Executable size: {} bytes", Files.size(executablePath));
//        }
//
//        return new CodeExecutionResult(true, null);
//    }
//
//    private CodeExecutionResult runCode(String language, Path codePath, Path workingDir, String input) throws IOException, InterruptedException {
//        String filename = codePath.getFileName().toString();
//        String output = getOutputFilename(language, filename);
//        String classname = CodeLanguageUtils.generateClassName(language);
//
//        // Handle different operating systems
//        String os = System.getProperty("os.name").toLowerCase();
//        boolean isWindows = os.contains("win");
//
//        ProcessBuilder pb;
//
//        logger.info("=== EXECUTION DEBUG ===");
//        logger.info("Language: {}", language);
//        logger.info("Working directory: {}", workingDir.toAbsolutePath());
//        logger.info("OS: {} (isWindows: {})", os, isWindows);
//        logger.info("Input provided: {}", input != null ? "'" + input + "'" : "null");
//
//        if ("cpp".equals(language) || "c".equals(language)) {
//            // For C/C++, run the compiled executable
//            Path executablePath = workingDir.resolve(output);
//            logger.info("Executable path: {}", executablePath.toAbsolutePath());
//
//            // Verify executable exists and is executable
//            if (!Files.exists(executablePath)) {
//                logger.error("Executable not found at: {}", executablePath.toAbsolutePath());
//                return new CodeExecutionResult(false, null, "Executable not found: " + executablePath.toString());
//            }
//
//            logger.info("Executable exists, size: {} bytes", Files.size(executablePath));
//
//            if (isWindows) {
//                // On Windows, run the .exe directly with full path
//                pb = new ProcessBuilder(executablePath.toAbsolutePath().toString());
//            } else {
//                // On Unix/Linux, use absolute path
//                pb = new ProcessBuilder(executablePath.toAbsolutePath().toString());
//                // Make sure it's executable on Unix systems
//                try {
//                    executablePath.toFile().setExecutable(true);
//                    logger.info("Set executable permission on Unix system");
//                } catch (Exception e) {
//                    logger.warn("Failed to set executable permission: {}", e.getMessage());
//                }
//            }
//        } else if ("java".equals(language)) {
//            // For Java, run with java command
//            logger.info("Running Java class: {}", classname);
//            pb = new ProcessBuilder("java", classname);
//        } else {
//            // For interpreted languages (Python, JavaScript)
//            String runCommand = CodeLanguageUtils.getRunCommand(language);
//            runCommand = runCommand
//                    .replace("{filename}", filename)
//                    .replace("{output}", output)
//                    .replace("{classname}", classname != null ? classname : "Solution");
//
//            logger.info("Running interpreted command: {}", runCommand);
//
//            // Split command properly for interpreted languages
//            List<String> commandList = new ArrayList<>();
//            String[] parts = runCommand.split("\\s+");
//            for (String part : parts) {
//                if (!part.trim().isEmpty()) {
//                    commandList.add(part);
//                }
//            }
//            pb = new ProcessBuilder(commandList);
//        }
//
//        pb.directory(workingDir.toFile());
//
//        logger.info("Starting process with command: {}", pb.command());
//        logger.info("Working directory for execution: {}", pb.directory().getAbsolutePath());
//
//        Process process = pb.start();
//
//        // Write input to process if provided
//        if (input != null && !input.isEmpty()) {
//            logger.info("Writing input to process: '{}'", input);
//            try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
//                writer.write(input);
//                writer.flush();
//            } catch (Exception e) {
//                logger.error("Error writing input to process: {}", e.getMessage());
//            }
//        } else {
//            // Close stdin if no input
//            try {
//                process.getOutputStream().close();
//            } catch (Exception e) {
//                logger.warn("Error closing process input stream: {}", e.getMessage());
//            }
//        }
//
//        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
//
//        if (!finished) {
//            logger.error("Process execution timeout after {} seconds", TIMEOUT_SECONDS);
//            process.destroyForcibly();
//            return new CodeExecutionResult(false, null, "Execution timeout");
//        }
//
//        String outputStr = readProcessOutput(process.getInputStream());
//        String errorStr = readProcessOutput(process.getErrorStream());
//
//        logger.info("Process finished");
//        logger.info("Process exit code: {}", process.exitValue());
//        logger.info("Process stdout length: {} chars", outputStr.length());
//        logger.info("Process stdout: '{}'", outputStr);
//        logger.info("Process stderr length: {} chars", errorStr.length());
//        logger.info("Process stderr: '{}'", errorStr);
//
//        if (process.exitValue() != 0) {
//            String errorMessage = "Runtime error (exit code: " + process.exitValue() + ")";
//            if (!errorStr.isEmpty()) {
//                errorMessage += ": " + errorStr;
//            } else if (!outputStr.isEmpty()) {
//                errorMessage += ": " + outputStr;
//            }
//            logger.error("Process failed: {}", errorMessage);
//            return new CodeExecutionResult(false, outputStr, errorMessage);
//        }
//
//        logger.info("Process completed successfully with output: '{}'", outputStr.trim());
//        return new CodeExecutionResult(true, outputStr.trim(), null);
//    }
//
//    private String getOutputFilename(String language, String filename) {
//        if ("java".equals(language)) {
//            // For Java, return the class name (without .class extension)
//            //String classNameMatch = code.match(/public\\s+class\\s+(\\w+)/);
//            return "Solution";
//        } else if ("cpp".equals(language) || "c".equals(language)) {
//            String baseName = filename.substring(0, filename.lastIndexOf('.'));
//            // Add .exe extension for Windows compatibility
//            String os = System.getProperty("os.name").toLowerCase();
//            if (os.contains("win")) {
//                return baseName + ".exe";
//            }
//            return baseName;
//        }
//        return filename;
//    }
//
//    private String readProcessOutput(InputStream inputStream) throws IOException {
//        StringBuilder output = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//        }
//        return output.toString();
//    }
//
//    private String generateSessionId() {
//        return "session_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
//    }
//
//    private void deleteDirectory(Path directory) throws IOException {
//        if (Files.exists(directory)) {
//            Files.walk(directory)
//                    .sorted((a, b) -> b.compareTo(a)) // Reverse order for deletion
//                    .forEach(path -> {
//                        try {
//                            Files.delete(path);
//                        } catch (IOException e) {
//                            logger.warn("Failed to delete: {}", path);
//                        }
//                    });
//        }
//    }
//}
package com.example.codingbattle.service;
import com.example.codingbattle.dto.CodeExecutionRequest;
import com.example.codingbattle.dto.CodeExecutionResult;
import com.example.codingbattle.util.CodeLanguageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CodeExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionService.class);
    private static final int TIMEOUT_SECONDS = 10;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "codingbattle";

    public CodeExecutionService() {
        // Create temp directory if it doesn't exist
        try {
            Path tempPath = Paths.get(TEMP_DIR);
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create temp directory: {}", e.getMessage());
        }
    }

    public CodeExecutionResult executeCode(CodeExecutionRequest request) {
        logger.info("Executing code for language: {}", request.getLanguage());

        if (!CodeLanguageUtils.isSupportedLanguage(request.getLanguage())) {
            return new CodeExecutionResult(false, null, "Unsupported language: " + request.getLanguage());
        }

        String sessionId = generateSessionId();
        Path workingDir = Paths.get(TEMP_DIR, sessionId);

        try {
            Files.createDirectories(workingDir);
            return executeCodeInDirectory(request, workingDir);
        } catch (Exception e) {
            logger.error("Error executing code: {}", e.getMessage(), e);
            return new CodeExecutionResult(false, null, "Execution error: " + e.getMessage());
        } finally {
            // Cleanup
            try {
                deleteDirectory(workingDir);
            } catch (IOException e) {
                logger.warn("Failed to cleanup directory: {}", e.getMessage());
            }
        }
    }

    private CodeExecutionResult executeCodeInDirectory(CodeExecutionRequest request, Path workingDir) throws IOException, InterruptedException {
        String language = request.getLanguage().toLowerCase();
        String code = request.getCode();
        String input = request.getInput() != null ? request.getInput() : "";

        // Generate filename with dynamic class name for Java
        String filename = CodeLanguageUtils.generateFileName(language, "Solution", code);
        Path codePath = workingDir.resolve(filename);

        // Write code to file
        Files.write(codePath, code.getBytes());

        long startTime = System.currentTimeMillis();

        // Compile if necessary
        if (CodeLanguageUtils.needsCompilation(language)) {
            CodeExecutionResult compileResult = compileCode(language, codePath, workingDir, code);
            if (!compileResult.isSuccess()) {
                return compileResult;
            }
        }

        // Execute code
        CodeExecutionResult result = runCode(language, codePath, workingDir, input, code);

        long executionTime = System.currentTimeMillis() - startTime;
        result.setExecutionTime(executionTime);

        return result;
    }

    private CodeExecutionResult compileCode(String language, Path codePath, Path workingDir, String code) throws IOException, InterruptedException {
        String compileCommand = CodeLanguageUtils.getCompileCommand(language);
        if (compileCommand == null) {
            return new CodeExecutionResult(true, null);
        }

        String filename = codePath.getFileName().toString();
        String output = getOutputFilename(language, filename, code);

        compileCommand = compileCommand
                .replace("{filename}", filename)
                .replace("{output}", output);

        logger.info("=== COMPILATION DEBUG ===");
        logger.info("Language: {}", language);
        logger.info("Working directory: {}", workingDir.toAbsolutePath());
        logger.info("Source file: {}", codePath.toAbsolutePath());
        logger.info("Expected output: {}", output);
        logger.info("Full compile command: {}", compileCommand);

        // Check if source file exists
        if (!Files.exists(codePath)) {
            logger.error("Source file does not exist: {}", codePath);
            return new CodeExecutionResult(false, null, "Source file not found: " + codePath);
        }

        // For Java, check if javac is available
        if ("java".equals(language)) {
            try {
                ProcessBuilder testPb = new ProcessBuilder("javac", "-version");
                Process testProcess = testPb.start();
                testProcess.waitFor(5, TimeUnit.SECONDS);
                // Note: javac -version outputs to stderr, not stdout, so we check differently
            } catch (Exception e) {
                logger.error("Error checking javac availability: {}", e.getMessage());
                return new CodeExecutionResult(false, null, "javac compiler not found: " + e.getMessage());
            }
        } else if ("cpp".equals(language) || "c".equals(language)) {
            // Check if g++ or gcc is available
            String compiler = "cpp".equals(language) ? "g++" : "gcc";
            try {
                ProcessBuilder testPb = new ProcessBuilder(compiler, "--version");
                Process testProcess = testPb.start();
                testProcess.waitFor(5, TimeUnit.SECONDS);
                if (testProcess.exitValue() != 0) {
                    logger.error("{} is not available or not working properly", compiler);
                    return new CodeExecutionResult(false, null, compiler + " compiler not found or not working");
                }
            } catch (Exception e) {
                logger.error("Error checking {} availability: {}", compiler, e.getMessage());
                return new CodeExecutionResult(false, null, compiler + " compiler check failed: " + e.getMessage());
            }
        }

        // Split command properly
        List<String> commandList = new ArrayList<>();
        String[] parts = compileCommand.split("\\s+");
        for (String part : parts) {
            commandList.add(part);
        }

        logger.info("Command list: {}", commandList);

        ProcessBuilder pb = new ProcessBuilder(commandList);
        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(false); // Separate stdout and stderr

        Process process = pb.start();
        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        String stdout = readProcessOutput(process.getInputStream());
        String stderr = readProcessOutput(process.getErrorStream());

        logger.info("Compilation finished: {}", finished);
        logger.info("Exit code: {}", process.exitValue());
        logger.info("Compilation stdout: {}", stdout);
        logger.info("Compilation stderr: {}", stderr);

        if (!finished) {
            process.destroyForcibly();
            return new CodeExecutionResult(false, null, "Compilation timeout");
        }

        if (process.exitValue() != 0) {
            String error = !stderr.isEmpty() ? stderr : stdout;
            logger.error("Compilation failed with error: {}", error);
            return new CodeExecutionResult(false, null, "Compilation error: " + error);
        }

        // Verify that the compiled output was created
        if ("java".equals(language)) {
            // For Java, check for .class file with dynamic class name
            String className = CodeLanguageUtils.generateClassName(language, code);
            Path classFile = workingDir.resolve(className + ".class");
            logger.info("Checking for Java class file at: {}", classFile.toAbsolutePath());
            logger.info("Dynamic class name detected: {}", className);

            if (!Files.exists(classFile)) {
                // List all files in the directory to see what was created
                try {
                    logger.info("Files in working directory:");
                    Files.list(workingDir).forEach(file -> logger.info("  - {}", file.getFileName()));
                } catch (IOException e) {
                    logger.warn("Could not list directory contents: {}", e.getMessage());
                }
                return new CodeExecutionResult(false, null, "Compilation succeeded but class file not found: " + className + ".class");
            }

            logger.info("Java compilation successful, class file created: {}", classFile.toAbsolutePath());
        } else {
            // For C/C++, check for executable
            Path executablePath = workingDir.resolve(output);
            logger.info("Checking for executable at: {}", executablePath.toAbsolutePath());

            if (!Files.exists(executablePath)) {
                // List all files in the directory to see what was created
                try {
                    logger.info("Files in working directory:");
                    Files.list(workingDir).forEach(file -> logger.info("  - {}", file.getFileName()));
                } catch (IOException e) {
                    logger.warn("Could not list directory contents: {}", e.getMessage());
                }
                return new CodeExecutionResult(false, null, "Compilation succeeded but executable not found: " + output);
            }

            logger.info("Compilation successful, executable created: {}", executablePath.toAbsolutePath());
            logger.info("Executable size: {} bytes", Files.size(executablePath));
        }

        return new CodeExecutionResult(true, null);
    }

//    private CodeExecutionResult runCode(String language, Path codePath, Path workingDir, String input, String code) throws IOException, InterruptedException {
//        String filename = codePath.getFileName().toString();
//        String output = getOutputFilename(language, filename, code);
//        String classname = CodeLanguageUtils.generateClassName(language, code);
//
//        // Handle different operating systems
//        String os = System.getProperty("os.name").toLowerCase();
//        boolean isWindows = os.contains("win");
//
//        ProcessBuilder pb;
//
//        logger.info("=== EXECUTION DEBUG ===");
//        logger.info("Language: {}", language);
//        logger.info("Working directory: {}", workingDir.toAbsolutePath());
//        logger.info("OS: {} (isWindows: {})", os, isWindows);
//        logger.info("Dynamic class name: {}", classname);
//        logger.info("Input provided: {}", input != null ? "'" + input + "'" : "null");
//
//        if ("cpp".equals(language) || "c".equals(language)) {
//            // For C/C++, run the compiled executable
//            Path executablePath = workingDir.resolve(output);
//            logger.info("Executable path: {}", executablePath.toAbsolutePath());
//
//            // Verify executable exists and is executable
//            if (!Files.exists(executablePath)) {
//                logger.error("Executable not found at: {}", executablePath.toAbsolutePath());
//                return new CodeExecutionResult(false, null, "Executable not found: " + executablePath.toString());
//            }
//
//            logger.info("Executable exists, size: {} bytes", Files.size(executablePath));
//
//            if (isWindows) {
//                // On Windows, run the .exe directly with full path
//                pb = new ProcessBuilder(executablePath.toAbsolutePath().toString());
//            } else {
//                // On Unix/Linux, use absolute path
//                pb = new ProcessBuilder(executablePath.toAbsolutePath().toString());
//                // Make sure it's executable on Unix systems
//                try {
//                    executablePath.toFile().setExecutable(true);
//                    logger.info("Set executable permission on Unix system");
//                } catch (Exception e) {
//                    logger.warn("Failed to set executable permission: {}", e.getMessage());
//                }
//            }
//        } else if ("java".equals(language)) {
//            // For Java, run with java command and dynamic class name
//            logger.info("Running Java class: {}", classname);
//            pb = new ProcessBuilder("java", classname);
//        } else {
//            // For interpreted languages (Python, JavaScript)
//            String runCommand = CodeLanguageUtils.getRunCommand(language);
//            runCommand = runCommand
//                    .replace("{filename}", filename)
//                    .replace("{output}", output)
//                    .replace("{classname}", classname != null ? classname : "Solution");
//
//            logger.info("Running interpreted command: {}", runCommand);
//
//            // Split command properly for interpreted languages
//            List<String> commandList = new ArrayList<>();
//            String[] parts = runCommand.split("\\s+");
//            for (String part : parts) {
//                if (!part.trim().isEmpty()) {
//                    commandList.add(part);
//                }
//            }
//            pb = new ProcessBuilder(commandList);
//        }
//
//        pb.directory(workingDir.toFile());
//
//        logger.info("Starting process with command: {}", pb.command());
//        logger.info("Working directory for execution: {}", pb.directory().getAbsolutePath());
//
//        Process process = pb.start();
//
//        // Write input to process if provided
//        if (input != null && !input.isEmpty()) {
//            logger.info("Writing input to process: '{}'", input);
//            try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
//                writer.write(input);
//                writer.flush();
//            } catch (Exception e) {
//                logger.error("Error writing input to process: {}", e.getMessage());
//            }
//        } else {
//            // Close stdin if no input
//            try {
//                process.getOutputStream().close();
//            } catch (Exception e) {
//                logger.warn("Error closing process input stream: {}", e.getMessage());
//            }
//        }
//
//        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
//
//        if (!finished) {
//            logger.error("Process execution timeout after {} seconds", TIMEOUT_SECONDS);
//            process.destroyForcibly();
//            return new CodeExecutionResult(false, null, "Execution timeout");
//        }
//
//        String outputStr = readProcessOutput(process.getInputStream());
//        String errorStr = readProcessOutput(process.getErrorStream());
//
//        logger.info("Process finished");
//        logger.info("Process exit code: {}", process.exitValue());
//        logger.info("Process stdout length: {} chars", outputStr.length());
//        logger.info("Process stdout: '{}'", outputStr);
//        logger.info("Process stderr length: {} chars", errorStr.length());
//        logger.info("Process stderr: '{}'", errorStr);
//
//        if (process.exitValue() != 0) {
//            String errorMessage = "Runtime error (exit code: " + process.exitValue() + ")";
//            if (!errorStr.isEmpty()) {
//                errorMessage += ": " + errorStr;
//            } else if (!outputStr.isEmpty()) {
//                errorMessage += ": " + outputStr;
//            }
//            logger.error("Process failed: {}", errorMessage);
//            return new CodeExecutionResult(false, outputStr, errorMessage);
//        }
//
//        logger.info("Process completed successfully with output: '{}'", outputStr.trim());
//        return new CodeExecutionResult(true, outputStr.trim(), null);
//    }
private CodeExecutionResult runCode(String language, Path codePath, Path workingDir, String input, String code) throws IOException, InterruptedException {
    String filename = codePath.getFileName().toString();
    String output = getOutputFilename(language, filename, code);
    String classname = CodeLanguageUtils.generateClassName(language, code);

    // Handle different operating systems
    String os = System.getProperty("os.name").toLowerCase();
    boolean isWindows = os.contains("win");

    ProcessBuilder pb;

    logger.info("=== EXECUTION DEBUG ===");
    logger.info("Language: {}", language);
    logger.info("Working directory: {}", workingDir.toAbsolutePath());
    logger.info("OS: {} (isWindows: {})", os, isWindows);
    logger.info("Dynamic class name: {}", classname);
    logger.info("Raw input provided: {}", input != null ? "'" + input + "'" : "null");

    if ("cpp".equals(language) || "c".equals(language)) {
        // For C/C++, run the compiled executable
        Path executablePath = workingDir.resolve(output);
        logger.info("Executable path: {}", executablePath.toAbsolutePath());

        // Verify executable exists and is executable
        if (!Files.exists(executablePath)) {
            logger.error("Executable not found at: {}", executablePath.toAbsolutePath());
            return new CodeExecutionResult(false, null, "Executable not found: " + executablePath.toString());
        }

        logger.info("Executable exists, size: {} bytes", Files.size(executablePath));

        if (isWindows) {
            pb = new ProcessBuilder(executablePath.toAbsolutePath().toString());
        } else {
            pb = new ProcessBuilder(executablePath.toAbsolutePath().toString());
            try {
                executablePath.toFile().setExecutable(true);
                logger.info("Set executable permission on Unix system");
            } catch (Exception e) {
                logger.warn("Failed to set executable permission: {}", e.getMessage());
            }
        }
    } else if ("java".equals(language)) {
        // For Java, run with java command and dynamic class name
        logger.info("Running Java class: {}", classname);
        pb = new ProcessBuilder("java", classname);
    } else {
        // For interpreted languages (Python, JavaScript)
        String runCommand = CodeLanguageUtils.getRunCommand(language);
        runCommand = runCommand
                .replace("{filename}", filename)
                .replace("{output}", output)
                .replace("{classname}", classname != null ? classname : "Solution");

        logger.info("Running interpreted command: {}", runCommand);

        List<String> commandList = new ArrayList<>();
        String[] parts = runCommand.split("\\s+");
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                commandList.add(part);
            }
        }
        pb = new ProcessBuilder(commandList);
    }

    pb.directory(workingDir.toFile());

    logger.info("Starting process with command: {}", pb.command());
    logger.info("Working directory for execution: {}", pb.directory().getAbsolutePath());

    Process process = pb.start();

    // Write input to process if provided (WITH SMART PYTHON INPUT HANDLING)
    if (input != null && !input.isEmpty()) {
        String processedInput = preprocessInput(input, language, code);
        logger.info("Writing processed input to process: '{}'", processedInput.replace("\n", "\\n"));
        try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
            writer.write(processedInput);
            writer.flush();
        } catch (Exception e) {
            logger.error("Error writing input to process: {}", e.getMessage());
        }
    } else {
        // Close stdin if no input
        try {
            process.getOutputStream().close();
        } catch (Exception e) {
            logger.warn("Error closing process input stream: {}", e.getMessage());
        }
    }

    boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

    if (!finished) {
        logger.error("Process execution timeout after {} seconds", TIMEOUT_SECONDS);
        process.destroyForcibly();
        return new CodeExecutionResult(false, null, "Execution timeout");
    }

    String outputStr = readProcessOutput(process.getInputStream());
    String errorStr = readProcessOutput(process.getErrorStream());

    logger.info("Process finished");
    logger.info("Process exit code: {}", process.exitValue());
    logger.info("Process stdout length: {} chars", outputStr.length());
    logger.info("Process stdout: '{}'", outputStr);
    logger.info("Process stderr length: {} chars", errorStr.length());
    logger.info("Process stderr: '{}'", errorStr);

    if (process.exitValue() != 0) {
        String errorMessage = "Runtime error (exit code: " + process.exitValue() + ")";
        if (!errorStr.isEmpty()) {
            errorMessage += ": " + errorStr;
        } else if (!outputStr.isEmpty()) {
            errorMessage += ": " + outputStr;
        }
        logger.error("Process failed: {}", errorMessage);
        return new CodeExecutionResult(false, outputStr, errorMessage);
    }

    logger.info("Process completed successfully with output: '{}'", outputStr.trim());
    return new CodeExecutionResult(true, outputStr.trim(), null);
}
    private String getOutputFilename(String language, String filename, String code) {
        if ("java".equals(language)) {
            // For Java, return the dynamic class name (without .class extension)
            String className = CodeLanguageUtils.generateClassName(language, code);
            return className != null ? className : "Solution";
        } else if ("cpp".equals(language) || "c".equals(language)) {
            String baseName = filename.substring(0, filename.lastIndexOf('.'));
            // Add .exe extension for Windows compatibility
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                return baseName + ".exe";
            }
            return baseName;
        }
        return filename;
    }

    private String readProcessOutput(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
    }
    private String preprocessInput(String input, String language, String code) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if ("python".equals(language)) {
            // Analyze the Python code to determine input pattern
            boolean hasInputSplit = code.contains("input().split()") || code.contains("input().split(");
            boolean hasMultipleInputs = code.split("input\\(\\)").length > 2; // Count input() occurrences

            if (hasInputSplit && !hasMultipleInputs) {
                // Single input().split() case - keep values on one line
                if (!input.endsWith("\n")) {
                    input = input + "\n";
                }
                logger.info("Python single-line input (with split) preprocessed: '{}'", input.replace("\n", "\\n"));
            } else {
                // Multiple separate input() calls - split values to separate lines
                String[] values = input.trim().split("\\s+");
                StringBuilder processedInput = new StringBuilder();

                for (String value : values) {
                    if (!value.isEmpty()) {
                        processedInput.append(value).append("\n");
                    }
                }

                String result = processedInput.toString();
                logger.info("Python multi-line input preprocessed from '{}' to '{}'", input, result.replace("\n", "\\n"));
                return result;
            }
        }

        return input;
    }

    private void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Reverse order for deletion
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            logger.warn("Failed to delete: {}", path);
                        }
                    });
        }
    }
}