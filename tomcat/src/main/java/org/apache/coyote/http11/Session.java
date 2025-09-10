package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    // key - "user", value - USER
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
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

    public void validateSessionUser(String sessionId, Object user) {
        Object sessionUser = values.get("user");
        if (this.id.equals(sessionId) && sessionUser != null && sessionUser.equals(user)) {
            return;
        }
        throw new IllegalArgumentException("[ERROR] cookie & user matching error");

    }

    public void invalidate() {
        
    }
}
