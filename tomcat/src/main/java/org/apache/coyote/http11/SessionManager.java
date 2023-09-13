package org.apache.coyote.http11;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> session = new ConcurrentHashMap<>();

    public static void add(final Session session) {
        SessionManager.session.put(session.getId(), session);
    }

    public static Optional<Session> findSession(final String id) {
        return Optional.ofNullable(session.get(id));
    }

    public static void remove(final String id) {
        session.remove(id);
    }

    private SessionManager() {
    }
}
