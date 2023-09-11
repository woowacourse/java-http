package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> session = new ConcurrentHashMap<>();

    public void add(final Session newSession) {
        session.put(newSession.getId(), newSession);
    }

    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return session.get(id);
    }

    public void remove(final String id) {
        session.remove(id);
    }
}
