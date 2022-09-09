package org.apache.coyote.http11.header;

import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> values;

    public HttpHeaders(final Map<String, String> values) {
        this.values = values;
    }
}
