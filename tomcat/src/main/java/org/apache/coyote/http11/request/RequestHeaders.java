package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeaders {

    private static final String ACCEPT = "Accept";

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

    public boolean isAcceptValueCss() {
        return isExistAccept() && (requestHeaders.get(ACCEPT).contains("text/css"));
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }
}
