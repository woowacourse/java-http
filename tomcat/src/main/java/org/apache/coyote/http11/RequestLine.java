package org.apache.coyote.http11;

import java.util.Objects;

public final class RequestLine {

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final String version;

    public RequestLine(HttpMethod method, RequestUri requestUri, String version) {
        this.method = Objects.requireNonNull(method);
        this.requestUri = Objects.requireNonNull(requestUri);
        this.version = Objects.requireNonNull(version);
    }

    public static RequestLine parse(String rawLine) {
        if (rawLine == null) {
            throw new IllegalArgumentException("잘못된 Request Line입니다.");
        }

        String[] parts = rawLine.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("잘못된 Request Line입니다.");
        }

        return new RequestLine(
                HttpMethod.of(parts[0]),
                new RequestUri(parts[1]),
                parts[2]
        );
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public String getVersion() {
        return version;
    }
}
