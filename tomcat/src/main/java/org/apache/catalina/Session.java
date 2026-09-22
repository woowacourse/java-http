package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> attributes;

    Session(String id) {
        this.id = id;
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}
