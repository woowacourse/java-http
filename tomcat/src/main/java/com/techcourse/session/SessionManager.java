package com.techcourse.session;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Optional<Session> findSession(String id) {
        return  Optional.ofNullable(SESSIONS.get(id));
    }

    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
