package org.apache.coyote.http11;

public class RequestLine {

    private final HttpRequestMethod method;
    private final String path;
    private final String protocolVersion;

    public RequestLine(String rawRequestLine) {
        String[] parts = rawRequestLine.split(" ", 3);
        this.method = HttpRequestMethod.valueOf(parts[0]);
        this.path = parts[1];
        this.protocolVersion = parts[2];
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String build() {
        return method + " " + path + " " + protocolVersion;
    }
}
