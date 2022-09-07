package org.apache.coyote.request;

import java.util.Map;

public class HttpHeader {

    private final Map<String, String> headers;

    private HttpHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeader from(Map<String, String> headers) {
        return new HttpHeader(headers);
    }
}
