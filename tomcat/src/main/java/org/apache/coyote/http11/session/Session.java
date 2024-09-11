package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private String id;
    private Map<String, Object> session;

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.session = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void addAttribute(String name, Object value) {
        session.put(name, value);
    }

    public Object getAttribute(String name) {
        return session.get(name);
    }

    public Object removeAttribute(String name) {
        return session.get(name);
    }

    public void invalidate() {
        session = new HashMap<>();
    }
}
