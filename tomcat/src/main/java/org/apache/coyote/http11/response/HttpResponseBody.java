package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponseBody {
    private final Map<String, String> bodies;

    public HttpResponseBody(Map<String, String> bodies) {
        this.bodies = bodies;
    }
}
