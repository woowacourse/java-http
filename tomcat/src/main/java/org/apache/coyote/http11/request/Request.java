package org.apache.coyote.http11.request;

public class Request {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final ResponseBody responseBody;

    public Request(RequestLine requestLine, RequestHeaders requestHeaders, ResponseBody responseBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.responseBody = responseBody;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }
}
