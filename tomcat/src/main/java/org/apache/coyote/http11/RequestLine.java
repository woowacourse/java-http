package org.apache.coyote.http11;

import java.util.Locale;

public class RequestLine {

    private final String method;
    private final String requestTarget;
    private final String protocol;

    public RequestLine(final String value) {
        final String[] parts = value.trim().split("\\s+", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청 라인입니다: " + value);
        }
        method = parts[0].toUpperCase(Locale.ROOT);
        requestTarget = parts[1];
        protocol = parts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getPath() {
        return requestTarget.split("\\?", 2)[0];
    }

    public String getProtocol() {
        return protocol;
    }
}
