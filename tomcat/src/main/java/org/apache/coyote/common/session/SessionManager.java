package org.apache.coyote.common.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    // static!
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final String key, final Session session) {
        SESSIONS.put(key, session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.values()
                .stream()
                .filter(session -> session.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Session does not exist."));
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}
