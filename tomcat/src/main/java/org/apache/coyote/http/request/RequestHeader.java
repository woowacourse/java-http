package org.apache.coyote.http.request;

import java.util.Collections;
import java.util.Map;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader(Map<String, String> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}
