package org.apache.catalina.manager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.Session;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public static Optional<Session> findSession(String id) {
        try {
            return Optional.of(SESSIONS.get(id));
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
