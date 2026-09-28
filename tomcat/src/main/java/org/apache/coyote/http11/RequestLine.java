package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;
    private final String queryString;
    private final String version;

    public RequestLine(final String requestLine) {
        final var parts = requestLine.split(" ");
        this.method = parts[0];
        final var uri = parts[1].split("\\?", 2);
        this.path = uri[0];
        this.queryString = uri.length == 2 ? uri[1] : "";
        this.version = parts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getVersion() {
        return version;
    }
}
