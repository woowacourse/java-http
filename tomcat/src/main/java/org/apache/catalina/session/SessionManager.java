package org.apache.catalina.session;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager INSTANCE;

    private final List<Session> sessions = new ArrayList<>();

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    private SessionManager() {
    }

    public String create(String key, Object value) {
        Session session = new Session();
        session.addAttribute(key, value);
        sessions.add(session);
        return session.getId();
    }

    public boolean contains(String id) {
        return sessions.stream()
                .anyMatch(session -> session.hasId(id));
    }
}
