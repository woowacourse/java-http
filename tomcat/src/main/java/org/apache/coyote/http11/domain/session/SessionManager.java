package org.apache.coyote.http11.domain.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        session.invalidate();
        SESSIONS.remove(session.getId());
    }
}
