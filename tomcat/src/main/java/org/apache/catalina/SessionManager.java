package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private final Map<String, Session> sessionMap;

    public SessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Session session) {
        sessionMap.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return sessionMap.get(id);
    }

    @Override
    public void remove(Session session) {
        sessionMap.remove(session.getId());
    }
}
