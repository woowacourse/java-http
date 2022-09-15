package org.springframework.config;

public class ApplicationConfig {

    private String basePackage;

    private ApplicationConfig() {
    }

    public String getBasePackage() {
        return basePackage;
    }
}
