package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.common.Session;

public class SessionManager implements Manager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        if (session == null) {
            return;
        }
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return sessions.getOrDefault(id, new Session());
    }

    @Override
    public void remove(final String id) {
        sessions.remove(id);
    }

    public void clear() {
        sessions.clear();
    }
}
