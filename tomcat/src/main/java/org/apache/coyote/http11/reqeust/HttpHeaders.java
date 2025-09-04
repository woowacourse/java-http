package org.apache.coyote.http11.reqeust;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(
            final String name,
            final String value
    ) {
        headers.put(name, value);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }
}
