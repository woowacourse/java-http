package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.request.headers.RequestHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

}
