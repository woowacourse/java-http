package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.IdGenerator;
import org.apache.catalina.Manager;
import org.apache.catalina.util.RandomIdGenerator;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final RandomIdGenerator DEFAULT_ID_GENERATOR = new RandomIdGenerator();

    private static SessionManager INSTANCE;

    private IdGenerator idGenerator;

    private SessionManager() {
        this.idGenerator = DEFAULT_ID_GENERATOR;
    }

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
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
        if (session == null) {
            return;
        }
        session.expire();
        SESSIONS.remove(session.getId());
    }

    @Override
    public Session createSession(String sessionId) {
        if (sessionId == null) {
            sessionId = idGenerator.generate();
        }
        long createdTime = System.currentTimeMillis();
        Session session = new Session(sessionId, createdTime);
        add(session);
        return session;
    }

    @Override
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
