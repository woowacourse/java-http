package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestHeaders {

    private final Map<String, String> headers;

    private HttpRequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders from(final Map<String, String> headers) {
        return new HttpRequestHeaders(headers);
    }

    public String get(final String header) {
        return headers.get(header);
    }
}
