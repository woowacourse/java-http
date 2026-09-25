package org.apache.coyote.http11.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Session {

    private final String id;
    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return SESSIONS.get(name);
    }

    public void setAttribute(final String name, final Session value) {
        SESSIONS.put(name, value);
    }

    public void removeAttribute(final String name) {
        SESSIONS.remove(name);
    }

    public void invalidate() {
        SESSIONS.clear();
        SessionManager.remove(this);
    }
}
