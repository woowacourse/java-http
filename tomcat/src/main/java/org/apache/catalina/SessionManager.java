package org.apache.catalina;

import nextstep.jwp.model.User;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {
    // static!
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public boolean isExistSession(final String id) {
        return SESSIONS.containsKey(id);
    }
}
