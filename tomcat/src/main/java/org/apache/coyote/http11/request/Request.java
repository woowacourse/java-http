package org.apache.coyote.http11.request;

public class Request {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final ResponseBody responseBod;

    public Request(RequestLine requestLine, RequestHeaders requestHeaders, ResponseBody responseBod) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.responseBod = responseBod;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public ResponseBody getResponseBod() {
        return responseBod;
    }
}
