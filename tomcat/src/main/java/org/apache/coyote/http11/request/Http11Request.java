package org.apache.coyote.http11.request;

public class Http11Request {
    private final HttpHeaders httpHeaders;
    private final HttpMethod httpMethod;

    public Http11Request(HttpHeaders httpHeaders, HttpMethod httpMethod) {
        this.httpHeaders = httpHeaders;
        this.httpMethod = httpMethod;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }
}
