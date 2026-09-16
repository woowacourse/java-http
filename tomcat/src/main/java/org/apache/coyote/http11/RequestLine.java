package org.apache.coyote.http11;

import java.util.Objects;

public class RequestLine {

    private final String method;
    private final String uri;
    private final String protocolVersion;

    public RequestLine(final String requestLine) {
        Objects.requireNonNull(requestLine, "요청의 첫 줄은 비어있을 수 없습니다.");

        final String[] components = requestLine.trim().split("\\s+");
        if (components.length != 3) {
            throw new IllegalArgumentException("잘못된 요청 첫 줄: " + requestLine);
        }

        this.method = components[0];
        this.uri = components[1];
        this.protocolVersion = components[2];
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
