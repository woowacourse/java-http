package org.apache.coyote.http11;

public class RequestLine {

    private static final int REQUEST_LINE_PARTS = 3;

    private final String method;
    private final String uri;
    private final String protocolVersion;

    private RequestLine(final String method, final String uri, final String protocolVersion
    ) {
        this.method = method;
        this.uri = uri;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine from(final String rawRequestLine) {
        if (rawRequestLine == null || rawRequestLine.isBlank()) {
            throw new IllegalArgumentException("Request Line이 비어있습니다.");
        }

        final String[] parts = rawRequestLine.trim().split(" ", REQUEST_LINE_PARTS);

        if (parts.length != REQUEST_LINE_PARTS) {
            throw new IllegalArgumentException("올바르지 않은 Request Line입니다: " + rawRequestLine);
        }

        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        final int queryIndex = uri.indexOf("?");

        if (queryIndex == -1) {
            return uri;
        }

        return uri.substring(0, queryIndex);
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}