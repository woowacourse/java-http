package org.apache.catalina.session;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) throws IOException {
        return sessions.get(id);
    }

    @Override
    public void remove(final Session session) {
        sessions.remove(session.getId());
    }

    public Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        add(session);
        return session;
    }
}
