package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeaders {

    private static final String ACCEPT = "Accept";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> requestHeaders;

    public RequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getAcceptHeaderValue() {
        if (isExistAccept()) {
            return requestHeaders.get(ACCEPT);
        }
        throw new IllegalArgumentException("header에 Accept 필드가 없습니다.");
    }

    private boolean isExistAccept() {
        return requestHeaders.containsKey(ACCEPT);
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }
}
