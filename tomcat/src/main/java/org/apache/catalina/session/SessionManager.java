package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();
    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

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
    public void remove(String id) {

    }

    @Override
    public void add(HttpSession session) {

    }

    @Override
    public void remove(HttpSession session) {

    }
}
