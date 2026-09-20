package org.apache.coyote.http11;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, HttpBody httpBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.httpBody = httpBody;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

}
