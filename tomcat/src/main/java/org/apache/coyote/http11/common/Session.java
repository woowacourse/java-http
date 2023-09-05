package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.SessionManager;

public class Session {

    private static final SessionManager SESSION_MANGER = new SessionManager();

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public boolean isNew() {
        return !SESSION_MANGER.contains(this.id);
    }

    public boolean isSaved() {
        return !isNew();
    }
}
