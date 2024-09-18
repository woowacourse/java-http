package org.apache.catalina.session;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SessionManager {

    private static SessionManager INSTANCE;

    private final List<Session> sessions = new CopyOnWriteArrayList<>();

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    private SessionManager() {
    }

    public synchronized String create(String key, Object value) {
        Session session = new Session();
        session.addAttribute(key, value);
        sessions.add(session);
        return session.getId();
    }

    public synchronized boolean contains(String id) {
        return sessions.stream()
                .anyMatch(session -> session.hasId(id));
    }
}
