package org.apache.coyote.http11.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> sessions = new HashMap<>();

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) throws IOException {
        return sessions.get(id);
    }

    @Override
    public void remove(final Session session) {
    }
}
