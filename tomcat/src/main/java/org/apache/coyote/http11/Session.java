package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> attributes;

    public Session() {
        this(UUID.randomUUID().toString());
    }

    public Session(final String id) {
        this.id = id;
        this.attributes = new HashMap<>();
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}
