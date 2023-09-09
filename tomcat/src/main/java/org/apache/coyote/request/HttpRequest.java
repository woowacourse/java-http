package org.apache.coyote.request;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpRequestHeader requestHeader;
    private final HttpRequestBody requestBody;

    public HttpRequest(HttpRequestLine requestLine, HttpRequestHeader requestHeader, HttpRequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }
}
