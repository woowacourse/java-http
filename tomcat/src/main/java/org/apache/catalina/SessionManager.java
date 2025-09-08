package org.apache.catalina;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) throws IOException {
        if (!SESSIONS.containsKey(id)) {
            throw new IOException("올바르지 않은 Session ID입니다.");
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean contains(final String id) {
        return SESSIONS.containsKey(id);
    }
}
