package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private String id;
    private Map<String, Object> values;

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.values = new HashMap<>();
    }

    public Session(String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void addAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public Object removeAttribute(String name) {
        return values.get(name);
    }

    public void invalidate() {
        values = new HashMap<>();
    }
}
