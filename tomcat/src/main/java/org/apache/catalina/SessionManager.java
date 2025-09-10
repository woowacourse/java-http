package org.apache.catalina;

import org.apache.coyote.http11.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager{

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) throws IOException {
        return SESSIONS.getOrDefault(id, null);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }
}
