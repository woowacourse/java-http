package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static SessionManager instance;
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}
