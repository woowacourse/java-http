package org.apache.coyote.http11.http;

public class HttpRequest {

    private final RequestFirstLine requestFirstLine;
    private final RequestHeader requestHeader;

    public HttpRequest(RequestFirstLine requestFirstLine, RequestHeader requestHeader) {
        this.requestFirstLine = requestFirstLine;
        this.requestHeader = requestHeader;
    }

    public String getRequestUri() {
        return requestFirstLine.getRequestUri();
    }

    public String findQueryStringValue(String key) {
        return requestFirstLine.findQueryStringValue(key);
    }
}
