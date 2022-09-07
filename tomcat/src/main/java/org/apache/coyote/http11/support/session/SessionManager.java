package org.apache.coyote.http11.support.session;

import org.apache.catalina.Manager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static SessionManager instance;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (Objects.isNull(instance)) {
            return new SessionManager();
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
}

