package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class RequestLine {
    public static final String DELIMITER = " ";
    public static final int METHOD_INDEX = 0;
    public static final int PATH_INDEX = 1;

    private final HttpMethod method;
    private final String path;

    private RequestLine(final HttpMethod method, final String path) {
        this.method = method;
        this.path = path;
    }

    public static RequestLine of(final String requestLine) {
        if (requestLine == null) {
            return null;
        }
        final var requestLineTokens = requestLine.split(DELIMITER);
        final var method = HttpMethod.valueOf(requestLineTokens[METHOD_INDEX]);
        final var path = requestLineTokens[PATH_INDEX];
        return new RequestLine(method, path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
