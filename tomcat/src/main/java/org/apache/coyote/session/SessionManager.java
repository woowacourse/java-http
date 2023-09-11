package org.apache.coyote.session;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

public class SessionManager {

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(final Session session) {
        SESSIONS.put(session.id(), session);
    }

    public static Session findSession(final String id) {
        if (isNull(id)) {
            return null;
        }

        return SESSIONS.getOrDefault(id, null);
    }

    public void remove(final Session session) {
        SESSIONS.remove(session.id());
    }
}
