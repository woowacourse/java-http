package org.apache.coyote.http11.support.session;

import org.apache.catalina.Manager;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static SessionManager instance;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new SessionManager();
        }
        return instance;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public int size() {
        return SESSIONS.size();
    }
}

