package org.apache.catalina.session;

import java.util.UUID;
import org.apache.catalina.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private static final SessionManager INSTANCE = new SessionManager();
    private final Map<String, Session> sessions = new HashMap<>();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        log.info("session added: {}", session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) throws IOException {
        return sessions.get(id);
    }

    @Override
    public void remove(final String id) {
        sessions.remove(id);
    }

    public Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    private SessionManager() {
    }
}
