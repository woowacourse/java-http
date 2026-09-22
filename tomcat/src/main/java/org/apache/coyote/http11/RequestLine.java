package org.apache.coyote.http11;

import java.net.URI;

public class RequestLine {

    private final String method;
    private final URI uri;
    private final String protocolVersion;

    public RequestLine(String requestLine) {
        String[] requestLineParts = parseRequestLine(requestLine);

        this.method = requestLineParts[0];
        this.uri = URI.create(requestLineParts[1]);
        this.protocolVersion = requestLineParts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getRawQuery() {
        return uri.getRawQuery();
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    private String[] parseRequestLine(String requestLine) {
        return requestLine.trim().split("\\s+", 3);
    }
}
