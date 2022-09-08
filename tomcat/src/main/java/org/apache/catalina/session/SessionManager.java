package org.apache.catalina.session;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions;

    private SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public static SessionManager instance() {
        return INSTANCE;
    }

    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        if (!sessions.containsKey(id)) {
            throw new NoSuchElementException("id에 해당하는 세션을 찾지 못했습니다 : " + id);
        }
        return sessions.get(id);
    }
}
