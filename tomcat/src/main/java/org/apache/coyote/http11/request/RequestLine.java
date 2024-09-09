package org.apache.coyote.http11.request;

public class RequestLine {
    public static final String DELIMITER = " ";

    private final String method;
    private final String path;

    private RequestLine(final String method, final String path) {
        this.method = method;
        this.path = path;
    }

    public static RequestLine of(final String requestLine) {
        final var method = requestLine.split(DELIMITER)[0];
        final var path = requestLine.split(DELIMITER)[1];
        return new RequestLine(method, path);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
