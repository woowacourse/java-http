package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private final RequestUri requestUri;
    private final RequestHeaders requestHeaders;

    private HttpRequest(String requestLine, Map<String, String> headers) {
        this.requestUri = RequestUri.from(requestLine);
        this.requestHeaders = new RequestHeaders(headers);
    }

    public static HttpRequest of(String requestLine, Map<String, String> headers) {
        return new HttpRequest(requestLine, headers);
    }

    public boolean isExistQueryString() {
        return requestUri.isExistQueryString();
    }

    public String getQueryStringValue(String key) {
        return requestUri.getQueryStringValue(key);
    }

    public boolean containsUri(String uri) {
        return requestUri.containsUri(uri);
    }

    public String getRequestUri() {
        return requestUri.getRequestUri();
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }
}
