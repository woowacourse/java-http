package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String uri;
    private final String httpVersion;

    public RequestLine(final String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 라인은 비어 있을 수 없습니다.");
        }

        final String[] parts = requestLine.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("잘못된 요청 라인입니다: " + requestLine);
        }

        this.method = parts[0];
        this.uri = parts[1];
        this.httpVersion = parts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
