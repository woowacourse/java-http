package org.apache.coyote.session;

import static java.util.UUID.randomUUID;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> attributes = new HashMap<>();
    private final String id;

    public Session() {
        this.id = randomUUID().toString();
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String id() {
        return id;
    }
}
