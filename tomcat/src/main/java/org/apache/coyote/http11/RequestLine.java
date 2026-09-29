package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;
    private final String protocolVersion;

    public RequestLine(
            final String method,
            final String path,
            final String protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] parts = requestLine.split(" ", 3);
        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
