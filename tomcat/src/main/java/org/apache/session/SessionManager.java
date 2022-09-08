package org.apache.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Optional<Session> find(String id) {
        if (SESSIONS.containsKey(id)) {
            return Optional.of(SESSIONS.get(id));
        }
        return Optional.empty();
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static Session generateNewSession() {
        UUID uuid = UUID.randomUUID();
        return new Session(uuid.toString());
    }

    public static boolean contains(String id) {
        return SESSIONS.containsKey(id);
    }
}
