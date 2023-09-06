package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseHeaders {

    private final List<HttpResponseHeaderField> headers;

    private HttpResponseHeaders(final List<HttpResponseHeaderField> headers) {
        this.headers = headers;
    }

    public static HttpResponseHeaders empty() {
        return new HttpResponseHeaders(new ArrayList<>());
    }

    public void add(final String key, final String value) {
        headers.add(new HttpResponseHeaderField(key, value));
    }

    public List<HttpResponseHeaderField> getHeaders() {
        return headers;
    }
}
