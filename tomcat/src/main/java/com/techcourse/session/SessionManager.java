package com.techcourse.session;

import com.techcourse.exception.IllegalConstructionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
        throw new IllegalConstructionException(this.getClass());
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean validate(String cookieId) {
        Session session = findSession(cookieId);

        return Objects.nonNull(session) && session.getId().equals(cookieId);
    }

    public static Session findSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }
}
