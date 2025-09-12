package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private String id;
    private final Map<String, Object> attributes = new HashMap<>();

    public Session() {
        this.id = UUID.randomUUID().toString();
    }

    public void changeId() {
        id = UUID.randomUUID().toString();
    }

    public void addAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public String getId() {
        return id;
    }
}
