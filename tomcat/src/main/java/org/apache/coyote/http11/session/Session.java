package org.apache.coyote.http11.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Session {

    private final String id;
    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    public void invalidate() {
        attributes.clear();
        SessionManager.remove(this);
    }
}
