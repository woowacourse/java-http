package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.catalina.Manager;


public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getSessionId(), session);
    }

    @Override
    public Session findSession(String sessionId) {
        Session session = SESSIONS.get(sessionId);
        if (Objects.isNull(session) || session.isExpired()) {
            SESSIONS.remove(sessionId);
            return null;
        }
        session.updateLastAccessTime();
        return session;
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getSessionId());
    }
}
