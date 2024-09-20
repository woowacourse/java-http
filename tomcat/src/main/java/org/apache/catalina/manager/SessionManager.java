package org.apache.catalina.manager;

import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static SessionManager instance = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
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
        SESSIONS.remove(session.getId());
    }
}
