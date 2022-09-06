package org.apache.coyote.http11;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final String body;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }

    public HttpMethod getMethod() {
        return requestLine.getHttpMethod();
    }

    public String getBody() {
        return body;
    }
}
