package com.techcourse.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionRepository {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void save(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Optional<Session> findById(final String id) {
        if (SESSIONS.containsKey(id)) {
            return Optional.ofNullable(SESSIONS.get(id));
        }
        return Optional.empty();
    }
}
