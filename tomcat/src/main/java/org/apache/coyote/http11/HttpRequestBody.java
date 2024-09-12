package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequestBody {
    private final Map<String, String> bodies;

    public HttpRequestBody(Map<String, String> bodies) {
        this.bodies = bodies;
    }
}
