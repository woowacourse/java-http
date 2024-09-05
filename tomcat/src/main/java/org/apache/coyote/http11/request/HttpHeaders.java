package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>();
    }
}
