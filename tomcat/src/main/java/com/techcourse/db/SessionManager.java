package com.techcourse.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public boolean addIfAbsent(Session session) {
        return SESSIONS.putIfAbsent(session.getId(), session) == null;
    }

    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean removeIfSame(Session session) {
        return SESSIONS.remove(session.getId(), session);
    }

    public void clear() {
        SESSIONS.clear();
    }
}
