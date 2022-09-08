package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> values;

    public HttpHeaders(final Map<String, String> values) {
        this.values = values;
    }
}
