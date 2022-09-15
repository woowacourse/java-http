package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    private Session(final String id) {
        this.id = id;
    }

    public static Session newInstance() {
        final var uuid = UUID.randomUUID().toString();
        final var session = new Session(uuid);
        SessionManager.add(session);
        return session;
    }

    public String getId() {
        return this.id;
    }

    public <T> T getAttribute(final String name) {
        return (T) values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        SessionManager.remove(this.id);
    }
}
