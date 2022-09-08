package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();

    private final Map<String, Session> Sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(Session session) {
        Sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return Sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        Sessions.remove(session.getId());
    }
}
