package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
}
