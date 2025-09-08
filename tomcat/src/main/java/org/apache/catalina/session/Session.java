package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private final String id = UUID.randomUUID().toString();
    private final Map<String, Object> attributes = new HashMap<>();


    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}
