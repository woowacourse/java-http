package org.apache.coyote.http11.request.model;

import org.apache.coyote.http11.request.HttpHeaders;

public class HttpRequest {

    private final HttpRequestStartLine startLine;
    private final HttpHeaders headers;

    public HttpRequest(final HttpRequestStartLine httpRequestStartLine, final HttpHeaders headers) {
        this.startLine = httpRequestStartLine;
        this.headers = headers;
    }

    public boolean isEqualToMethod(final HttpMethod httpMethod) {
        return startLine.isEqualToMethod(httpMethod);
    }

    public boolean isEqualToUri(final String uri) {
        return startLine.isEqualToUri(uri);
    }

    public boolean isQueryString() {
        return startLine.isQueryString();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public HttpRequestUri getUri() {
        return startLine.getUri();
    }

    public HttpVersion getVersion() {
        return startLine.getVersion();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
