package org.apache.catalina;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class StaticResource {

    private static final String STATIC_ROOT = "static/";

    public Optional<byte[]> read(String requestPath) throws IOException {
        String resourcePath = find(requestPath);
        if (resourcePath == null) {
            return Optional.empty();
        }

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return Optional.empty();
            }
            return Optional.of(inputStream.readAllBytes());
        }
    }

    private String find(String requestPath) {
        String resourcePath = normalize(requestPath);
        String fullPath = STATIC_ROOT + resourcePath;
        if (getClass().getClassLoader().getResource(fullPath) != null) {
            return fullPath;
        }
        if (resourcePath.endsWith(".html")) {
            return null;
        }

        String htmlPath = fullPath + ".html";
        if (getClass().getClassLoader().getResource(htmlPath) != null) {
            return htmlPath;
        }
        return null;
    }

    private String normalize(String requestPath) {
        String resourcePath = requestPath.startsWith("/") ? requestPath.substring(1) : requestPath;
        if (resourcePath.contains("..")) {
            throw new IllegalArgumentException("Invalid resource path: " + requestPath);
        }
        return resourcePath;
    }
}
