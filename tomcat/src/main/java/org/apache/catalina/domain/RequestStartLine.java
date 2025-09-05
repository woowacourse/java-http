package org.apache.catalina.domain;

import com.techcourse.exception.BadRequestException;
import java.util.List;

public record RequestStartLine(String method, String path, String version) {

    private static final int STARTLINE_SIZE = 3;

    public static RequestStartLine from(List<String> requestLines) {
        validateStartLine(requestLines);
        final String startLine = requestLines.getFirst();

        final String[] tokens = startLine.split(" ");
        if (tokens.length != STARTLINE_SIZE) {
            throw new BadRequestException("Invalid start line: " + startLine);
        }

        final String method = tokens[0];
        final String path = parsePath(tokens[1]);
        final String version = tokens[2];

        return new RequestStartLine(method, path, version);
    }

    private static void validateStartLine(List<String> requestLines) {
        if (requestLines == null || requestLines.isEmpty() || requestLines.getFirst() == null) {
            throw new BadRequestException("Request lines are null or empty");
        }

        final String startLine = requestLines.getFirst();
        if (startLine == null || startLine.isBlank()) {
            throw new BadRequestException("Start line is null or empty");
        }
    }

    private static String parsePath(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return "/index.html";
        }

        return path.split("\\?")[0];
    }
}
