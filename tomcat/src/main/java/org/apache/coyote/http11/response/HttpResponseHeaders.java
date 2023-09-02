package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponseHeaders {

    private final Map<String, String> headers;

    private HttpResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpResponseHeaders from(final Map<String, String> headers) {
        return new HttpResponseHeaders(headers);
    }

    public String get(final String header) {
        return headers.get(header);
    }
}
