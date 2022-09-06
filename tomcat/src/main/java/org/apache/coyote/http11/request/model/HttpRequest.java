package org.apache.coyote.http11.request.model;

import org.apache.coyote.http11.request.HttpHeaders;

public class HttpRequest {

    private final HttpRequestLine startLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(final HttpRequestLine httpRequestStartLine, final HttpHeaders headers, final String body) {
        this.startLine = httpRequestStartLine;
        this.headers = headers;
        this.body = body;
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

    public HttpPath getUri() {
        return startLine.getPath();
    }

    public HttpVersion getVersion() {
        return startLine.getVersion();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
