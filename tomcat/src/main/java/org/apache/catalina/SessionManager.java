package org.apache.catalina;

import nextstep.jwp.model.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();
    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public void addLoginSession(final String jSessionId, final User user) {
        Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        instance.add(session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.getOrDefault(id, null);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId(), session);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}
