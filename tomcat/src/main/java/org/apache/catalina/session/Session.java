package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();
    private volatile boolean isNew = true;

    public Session(String id) {
        this.id = id;
    }

    public void access() {
        this.isNew = false;
    }

    public boolean isNew() {
        return isNew;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, Object value) {
        if (value == null) {
            removeAttribute(name);
            return;
        }
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
        SessionManager.getInstance().remove(id);
    }
}
