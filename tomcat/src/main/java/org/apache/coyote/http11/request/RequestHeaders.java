package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getHeaderValue(String field) {
        return headers.get(field);
    }
}
