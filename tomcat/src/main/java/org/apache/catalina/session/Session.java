package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private final String id;
    private final SessionManager sessionManager;
    private final Map<String, Object> values = new HashMap<>();

    public Session(SessionManager sessionManager) {
        this.id = UUID.randomUUID().toString();
        this.sessionManager = sessionManager;
    }


    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
        sessionManager.remove(this);
    }
}
