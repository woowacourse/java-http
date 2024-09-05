package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestBody {
    Map<String, String> bodies;

    public RequestBody(final Map<String, String> bodies) {
        this.bodies = bodies;
    }

    public String getAttribute(final String name) {
        return bodies.get(name);
    }
}
