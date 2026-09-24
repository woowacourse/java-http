package org.apache.coyote.http11.request;

public class RequestLine {

    private final String method;
    private final String uri;
    private final String protocolVersion;

    private RequestLine(String method, String uri, String protocolVersion) {
        this.method = method;
        this.uri = uri;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine parse(final String line) {
        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("HTTP 요청 라인이 비어있습니다.");
        }

        final var tokens = line.trim().split("\\s+");

        if (tokens.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청 라인입니다.");
        }

        return new RequestLine(tokens[0], tokens[1], tokens[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
