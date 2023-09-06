package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public static Session createSession(String key) {
        Session session = new Session(key);
        SESSIONS.put(key, session);
        return session;
    }
}
