package org.apache.catalina;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class Session {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public static Session newSession() {
        UUID uuid = UUID.randomUUID();
        return new Session(uuid.toString());
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final User user) {
        values.put(name, user);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        SessionManager.remove(id);
    }
}
