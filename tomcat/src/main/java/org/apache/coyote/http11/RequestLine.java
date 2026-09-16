package org.apache.coyote.http11;

public record RequestLine(
        HttpMethod method,
        String requestTarget,
        String httpVersion
) {
    private static final int REQUEST_LINE_PARTS_SIZE = 3;

    public static RequestLine from(final String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("HTTP request line must not be blank");
        }

        final String[] requestLineParts = requestLine.strip().split(" ");
        if (requestLineParts.length != REQUEST_LINE_PARTS_SIZE) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + requestLine);
        }

        return new RequestLine(
                HttpMethod.valueOf(requestLineParts[0]),
                requestLineParts[1],
                requestLineParts[2]
        );
    }
}
