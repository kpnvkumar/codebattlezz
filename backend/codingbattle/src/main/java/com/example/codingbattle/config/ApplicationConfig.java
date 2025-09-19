package com.example.codingbattle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
class ApplicationConfig {

    @Bean
    @ConfigurationProperties(prefix = "app")
    public AppProperties appProperties() {
        return new AppProperties();
    }

    public static class AppProperties {
        private String name = "Coding Battle";
        private String version = "1.0.0";
        private int maxExecutionTimeSeconds = 10;
        private int maxOutputSizeBytes = 1024 * 1024; // 1MB

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public int getMaxExecutionTimeSeconds() { return maxExecutionTimeSeconds; }
        public void setMaxExecutionTimeSeconds(int maxExecutionTimeSeconds) {
            this.maxExecutionTimeSeconds = maxExecutionTimeSeconds;
        }

        public int getMaxOutputSizeBytes() { return maxOutputSizeBytes; }
        public void setMaxOutputSizeBytes(int maxOutputSizeBytes) {
            this.maxOutputSizeBytes = maxOutputSizeBytes;
        }
    }
}

