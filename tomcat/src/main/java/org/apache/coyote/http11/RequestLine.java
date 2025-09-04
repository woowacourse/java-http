package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;
    private final String protocol;

    public RequestLine(final String requestLine) {
        String[] requestLineParts = validateAndParseRequestLine(requestLine);
        this.method = requestLineParts[0];
        this.path = requestLineParts[1];
        this.protocol = requestLineParts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    private String[] validateAndParseRequestLine(final String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Request line 값이 null 또는 비어있습니다.");
        }
        String[] parts = requestLine.trim().split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Request line 형식이 올바르지 않습니다.");
        }
        return parts;
    }
}
