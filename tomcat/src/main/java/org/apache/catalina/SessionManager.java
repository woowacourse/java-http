package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.security.Session;

public class SessionManager implements Manager {

    private static final Manager manager = new SessionManager();
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Manager getInstance() {
        return manager;
    }

    @Override
    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.getId());
    }

}
