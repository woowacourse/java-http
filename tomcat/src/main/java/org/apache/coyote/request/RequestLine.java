package org.apache.coyote.request;

import java.util.Objects;

public class RequestLine {

    private final String method;
    private final String path;
    private final String queryString;
    private final String protocol;

    private RequestLine(final String method, final String path, final String queryString, final String protocol) {
        Objects.requireNonNull(method, "method must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(queryString, "queryString must not be null");
        Objects.requireNonNull(protocol, "protocol must not be null");
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.protocol = protocol;
    }

    public static RequestLine from(final String line) {
        final var parts = line.split(" ");
        final var method = parts[0];

        final var uri = parts[1];
        final var queryIndex = uri.indexOf('?');
        final var protocol = parts[2];

        final String path;
        final String queryString;

        if (queryIndex != -1) {
            path = uri.substring(0, queryIndex);
            queryString = uri.substring(queryIndex + 1);
        } else {
            path = uri;
            queryString = "";
        }

        return new RequestLine(method, path, queryString, protocol);
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

    public String getProtocol() {
        return protocol;
    }
}
