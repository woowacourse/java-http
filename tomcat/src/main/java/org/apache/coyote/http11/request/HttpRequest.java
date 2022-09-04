package org.apache.coyote.http11.request;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
    }
}
