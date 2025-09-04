package org.apache.catalina.domain;

public record StartLine(String method, String path, String version) {

    private static final int STARTLINE_SIZE = 3;

    public static StartLine from(String startLine) {
        if (startLine == null || startLine.isBlank()) {
            throw new IllegalArgumentException("Start line is null or empty");
        }

        final String[] tokens = startLine.split(" ");
        if (tokens.length != STARTLINE_SIZE) {
            throw new IllegalArgumentException("Invalid start line: " + startLine);
        }

        final String method = tokens[0];
        final String path = parsePath(tokens[1]);
        final String version = tokens[2];

        return new StartLine(method, path, version);
    }

    private static String parsePath(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return "/index.html";
        }

        return path.split("\\?")[0];
    }

}
