package org.apache.coyote.http11.http;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(final HttpRequestLine httpRequestStartLine, final HttpHeaders headers, final String body) {
        this.requestLine = httpRequestStartLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpPath getPath() {
        return requestLine.getPath();
    }

    public HttpVersion getVersion() {
        return requestLine.getVersion();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public boolean isEqualToMethod(final HttpMethod httpMethod) {
        return requestLine.isEqualToMethod(httpMethod);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                '}';
    }
}
