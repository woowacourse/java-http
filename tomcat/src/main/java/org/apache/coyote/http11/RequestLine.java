package org.apache.coyote.http11;

import java.util.Objects;

public class RequestLine {

    private final String method;
    private final String path;
    private final String queryString;
    private final String protocolVersion;

    public RequestLine(final String requestLine) {
        Objects.requireNonNull(requestLine, "요청의 첫 줄은 비어있을 수 없습니다.");

        final String[] components = requestLine.trim().split(" ");
        if (components.length != 3) {
            throw new IllegalArgumentException("잘못된 요청 첫 줄: " + requestLine);
        }

        this.method = components[0];
        this.protocolVersion = components[2];

        final String requestUri = components[1];
        final int queryIndex = requestUri.indexOf('?');
        if (queryIndex >= 0) {
            this.path = requestUri.substring(0, queryIndex);
            this.queryString = requestUri.substring(queryIndex + 1);
        } else {
            this.path = requestUri;
            this.queryString = "";
        }
    }

    public boolean isPost() {
        return "POST".equals(method);
    }

    public boolean isGet() {
        return "GET".equals(method);
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
}
