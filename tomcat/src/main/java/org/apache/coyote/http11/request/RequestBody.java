package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestBody {

    private final Map<String, String> bodies;

    public RequestBody(Map<String, String> bodies) {
        this.bodies = bodies;
    }

    public boolean isEmpty() {
        return bodies.isEmpty();
    }

    public String getValue(String field) {
        return bodies.get(field);
    }
}
