package com.example.codingbattle.util;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeLanguageUtils {

    private static final Logger logger = LoggerFactory.getLogger(CodeLanguageUtils.class);

    public static final List<String> SUPPORTED_LANGUAGES = Arrays.asList(
            "java", "python", "cpp", "c", "javascript"
    );

    private static final Map<String, String> FILE_EXTENSIONS = new HashMap<>();
    private static final Map<String, String> COMPILE_COMMANDS = new HashMap<>();
    private static final Map<String, String> RUN_COMMANDS = new HashMap<>();

    // Pattern to match Java class declarations - prioritize public classes
    private static final Pattern JAVA_PUBLIC_CLASS_PATTERN = Pattern.compile(
            "public\\s+class\\s+(\\w+)",
            Pattern.MULTILINE
    );

    private static final Pattern JAVA_CLASS_PATTERN = Pattern.compile(
            "(?:^|\\s)class\\s+(\\w+)",
            Pattern.MULTILINE
    );

    static {
        // File extensions
        FILE_EXTENSIONS.put("java", ".java");
        FILE_EXTENSIONS.put("python", ".py");
        FILE_EXTENSIONS.put("cpp", ".cpp");
        FILE_EXTENSIONS.put("c", ".c");
        FILE_EXTENSIONS.put("javascript", ".js");

        // Fixed compile commands for your system
        COMPILE_COMMANDS.put("java", "javac {filename}");
        COMPILE_COMMANDS.put("cpp", "g++ -std=c++17 -O2 -Wall -static-libgcc -static-libstdc++ -o {output} {filename}");
        COMPILE_COMMANDS.put("c", "gcc -std=c11 -O2 -Wall -o {output} {filename}");

        // Fixed run commands for your system
        RUN_COMMANDS.put("java", "java {classname}");
        RUN_COMMANDS.put("python", "python {filename}");
        RUN_COMMANDS.put("cpp", "{output}");
        RUN_COMMANDS.put("c", "{output}");
        RUN_COMMANDS.put("javascript", "node {filename}");
    }

    public static boolean isSupportedLanguage(String language) {
        return SUPPORTED_LANGUAGES.contains(language.toLowerCase());
    }

    public static String getFileExtension(String language) {
        return FILE_EXTENSIONS.get(language.toLowerCase());
    }

    public static String getCompileCommand(String language) {
        return COMPILE_COMMANDS.get(language.toLowerCase());
    }

    public static String getRunCommand(String language) {
        return RUN_COMMANDS.get(language.toLowerCase());
    }

    public static boolean needsCompilation(String language) {
        return COMPILE_COMMANDS.containsKey(language.toLowerCase());
    }

    /**
     * Generates filename based on the actual class name extracted from Java code,
     * or uses the provided prefix for other languages
     */
    public static String generateFileName(String language, String prefix, String code) {
        String extension = getFileExtension(language);
        if (extension == null) {
            extension = ".txt";
        }

        if ("java".equals(language.toLowerCase()) && code != null) {
            String className = extractJavaClassName(code);
            if (className != null) {
                return className + extension;
            }
        }

        return prefix + extension;
    }

    /**
     * Legacy method for backward compatibility
     */
    public static String generateFileName(String language, String prefix) {
        return generateFileName(language, prefix, null);
    }

    /**
     * Generates class name dynamically from Java code, falls back to default
     */
    public static String generateClassName(String language, String code) {
        if ("java".equals(language.toLowerCase()) && code != null) {
            String className = extractJavaClassName(code);
            return className != null ? className : "Solution";
        }
        return null;
    }

    /**
     * Legacy method for backward compatibility - returns hardcoded "Solution"
     */
    public static String generateClassName(String language) {
        return generateClassName(language, null);
    }

    /**
     * Extracts the class name from Java source code - prioritizes public classes
     */
    public static String extractJavaClassName(String javaCode) {
        if (javaCode == null || javaCode.trim().isEmpty()) {
            return null;
        }

        // First, try to find a public class (this takes precedence)
        Matcher publicMatcher = JAVA_PUBLIC_CLASS_PATTERN.matcher(javaCode);
        if (publicMatcher.find()) {
            String publicClassName = publicMatcher.group(1);
            logger.debug("Found public class: {}", publicClassName);
            return publicClassName;
        }

        // If no public class, fall back to any class
        Matcher matcher = JAVA_CLASS_PATTERN.matcher(javaCode);
        if (matcher.find()) {
            String className = matcher.group(1);
            logger.debug("Found non-public class: {}", className);
            return className;
        }

        logger.debug("No class found in Java code");
        return null;
    }

    public static List<String> getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }
}