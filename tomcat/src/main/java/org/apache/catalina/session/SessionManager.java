package org.apache.catalina.session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    // static!
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager sessionManager = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return sessionManager;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) throws IOException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}
