package org.apache.catalina.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public static Session create() {
        final UUID uuid = UUID.randomUUID();
        final Session session = new Session(uuid.toString());
        SESSION_MANAGER.add(session);

        return session;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public boolean containsAttribute(final String name) {
        return values.containsKey(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
    }
}
