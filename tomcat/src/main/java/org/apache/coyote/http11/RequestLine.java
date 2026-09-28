package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;

public final class RequestLine {

    private final String method;
    private final String uri;
    private final String path;

    public RequestLine(final String requestLine) {
        final var parts = requestLine.trim().split("\\s+");
        if (parts.length != 3) {
            throw new UncheckedServletException(
                    new IllegalArgumentException("유효하지 않은 요청 라인입니다: " + requestLine)
            );
        }

        method = parts[0];
        uri = parts[1];

        final var queryIndex = uri.indexOf('?');
        path = queryIndex < 0 ? uri : uri.substring(0, queryIndex);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }
}
