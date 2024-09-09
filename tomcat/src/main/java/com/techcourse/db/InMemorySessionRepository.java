package com.techcourse.db;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.auth.Session;

public class InMemorySessionRepository {
    private static final List<Session> database = new ArrayList<>();

    public static void save(Session session) {
        database.add(session);
    }

    public static boolean existsById(String sessionId) {
        return database.stream()
                .map(Session::getId)
                .anyMatch(id -> id.equals(sessionId));
    }
}
