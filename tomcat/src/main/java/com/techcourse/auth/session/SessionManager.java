package com.techcourse.auth.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session find(final String id) {
        return SESSIONS.get(id);
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public void invalidate(String id) {
        Session session = find(id);
        if (session != null) {
            remove(id);
        }
    }
}
