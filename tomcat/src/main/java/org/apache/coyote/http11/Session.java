package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {

    public static final String JSESSIONID = "JSESSIONID";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    private Session(final String id) {
        this.id = id;
    }

    public static Session create(String id) {
        Session session = new Session(id);
        new SessionManager().add(session);
        return session;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        return null;
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        new SessionManager().remove(this);
    }
}
