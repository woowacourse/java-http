package org.apache.coyote.http11;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader httpHeader;

    public HttpRequest(RequestLine requestLine, HttpHeader httpHeader) {
        this.requestLine = requestLine;
        this.httpHeader = httpHeader;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }
}
