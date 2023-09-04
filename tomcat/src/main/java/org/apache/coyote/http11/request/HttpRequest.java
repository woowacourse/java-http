package org.apache.coyote.http11.request;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(
            RequestLine requestLine,
            RequestHeaders requestHeaders,
            RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
