package com.http.domain;

public record StartLine(String method, String path, String version) {

    public static StartLine from(String startLine) {
        if (startLine == null || startLine.isBlank()) {
            throw new IllegalArgumentException("Start line is null or empty");
        }

        final String[] tokens = startLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid start line: " + startLine);
        }

        final String method = tokens[0];
        final String path = parsePath(tokens[1]);
        final String version = tokens[2];

        return new StartLine(method, path, version);
    }

    private static String parsePath(String path) {
        if (path.isEmpty()) {
            return "/";
        }

        return path.split("\\?")[0];
    }

}
