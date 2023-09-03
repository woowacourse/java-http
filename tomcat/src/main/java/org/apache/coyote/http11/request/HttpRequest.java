package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.request.headers.RequestHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
    }

}
