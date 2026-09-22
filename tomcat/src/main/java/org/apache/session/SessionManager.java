package org.apache.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void add(Session session) {
        sessions.put(session.getJsessionId(), session);
    }

    public Session findSession(String jsessionId) {
        if (jsessionId == null) {
            return null;
        }
        return sessions.get(jsessionId);
    }

    public void remove(String jsessionId) {
        if (jsessionId != null) {
            sessions.remove(jsessionId);
        }
    }
}
