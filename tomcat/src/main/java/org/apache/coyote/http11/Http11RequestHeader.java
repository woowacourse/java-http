package org.apache.coyote.http11;

import java.util.Map;

public class Http11RequestHeader {

    private final Map<String, String> headers;

    public Http11RequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public String get(String name) {
        return headers.get(name);
    }
}
