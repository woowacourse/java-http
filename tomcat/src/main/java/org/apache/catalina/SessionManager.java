package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        if (sessions.containsKey(id)) {
            return sessions.get(id);
        }
        throw new IllegalArgumentException("요청한 세션을 찾을 수 없습니다.");
    }

    @Override
    public void remove(String id) {
        sessions.remove(id);
    }
}
