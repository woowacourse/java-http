package org.apache.coyote.http11;

import org.apache.catalina.Manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    @Override
    public Session findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    private SessionManager() {}
}
