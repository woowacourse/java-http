package org.apache.coyote.http11.http;

public class HttpRequest {

    private final HttpRequestLine startLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(final HttpRequestLine httpRequestStartLine, final HttpHeaders headers, final String body) {
        this.startLine = httpRequestStartLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public HttpPath getPath() {
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
