package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Http11Session> SESSIONS = new HashMap<>();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Http11Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Http11Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public boolean containsSession(final String id) {
        return SESSIONS.containsKey(id);
    }

    private SessionManager() {}
}
