package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.Headers;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    public HttpRequest(
            final RequestLine requestLine,
            final Headers requestHeader,
            final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.headers = requestHeader;
        this.requestBody = requestBody;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
