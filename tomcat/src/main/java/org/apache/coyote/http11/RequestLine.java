package org.apache.coyote.http11;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final String protocolVersion;

    public RequestLine(final HttpMethod method, final String path, final String protocolVersion) {
        this.method = method;
        this.path = parsedPath(path);
        this.protocolVersion = protocolVersion;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String parsedPath(final String path) {
        if (path.contains("?")) {
            return path.split("\\?")[0];
        }
        return path;
    }
}
