package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> requestHeaders;

    public RequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }
}
