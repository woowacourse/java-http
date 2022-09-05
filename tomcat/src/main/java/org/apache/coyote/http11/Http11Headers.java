package org.apache.coyote.http11;

import java.util.Map;

public class Http11Headers {

    private final Map<String, String> headers;

    public Http11Headers(final Map<String, String> headers) {
        this.headers = headers;
    }
}
