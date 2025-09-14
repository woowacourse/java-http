package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String sessionId;
    private final Map<String, Object> values = new HashMap<>();

    public static Session createNewSession() {
        return new Session(generateSessionId());
    }

    private static String generateSessionId() {
        return java.util.UUID.randomUUID().toString();
    }

    private Session(final String id) {
        this.sessionId = id;
    }

    public String getId() {
        return sessionId;
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
    }
}
