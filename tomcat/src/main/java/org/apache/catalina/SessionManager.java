package org.apache.catalina;

import nextstep.jwp.model.User;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {
    private static final SessionManager INSTANCE = new SessionManager();
    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public String createSession(final User user) {
        final UUID uuid = UUID.randomUUID();
        final String id = uuid.toString();
        final Session session = new Session(id);
        session.setAttribute("user", user);
        SESSIONS.putIfAbsent(id, session);
        return id;
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    @Override
    public void add(final Session session) {
        SESSIONS.putIfAbsent(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }
}
