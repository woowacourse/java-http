package com.techcourse.model;

import java.util.List;

public class RequestLine {

    private static final List<String> methods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");

    private final String method;
    private final String path;
    private final String version;

    public static RequestLine of(String requestLine) {
        return new RequestLine(List.of(requestLine.split(" ")));
    }

    private RequestLine(List<String> requestLines) {
        validateSize(requestLines);
        String originMethod = requestLines.get(0);
        String originPath = requestLines.get(1);
        String originVersion = requestLines.get(2);
        validateMethod(originMethod);
        validatePath(originPath);
        this.method = originMethod;
        this.path = originPath;
        this.version = originVersion;
    }

    private void validateSize(List<String> requestLines) {
        if (requestLines.size() != 3) {
            throw new IllegalArgumentException("Invalid request line: " + requestLines);
        }
    }

    private void validateMethod(String method) {
        if (method == null || method.isEmpty()) {
            throw new IllegalArgumentException("Method cannot be null or empty");
        }
        if (!methods.contains(method)) {
            throw new IllegalArgumentException("Method " + method + " is not supported");
        }
    }

    private void validatePath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with '/'");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
