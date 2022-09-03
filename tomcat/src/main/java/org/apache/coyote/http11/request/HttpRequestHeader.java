package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestHeader {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;

    private HttpRequestHeader(String requestLine, Map<String, String> headers) {
        this.requestLine = new RequestLine(requestLine);
        this.requestHeaders = new RequestHeaders(headers);
    }

    public static HttpRequestHeader of(String requestLine, Map<String, String> headers) {
        return new HttpRequestHeader(requestLine, headers);
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }
}
