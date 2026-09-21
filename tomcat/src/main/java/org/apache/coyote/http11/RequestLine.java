package org.apache.coyote.http11;

public final class RequestLine {

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final String version;

    public RequestLine(String rawLine) {
        if (rawLine == null) {
            throw new IllegalArgumentException("잘못된 Request Line입니다.");
        }

        String[] parts = rawLine.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("잘못된 Request Line입니다.");
        }

        this.method = HttpMethod.of(parts[0]);
        this.requestUri = new RequestUri(parts[1]);
        this.version = parts[2];
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
