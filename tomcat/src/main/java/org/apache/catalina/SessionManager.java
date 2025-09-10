package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {};

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void setSession(String key, Session session) {
        SESSIONS.put(key, session);
    }

    public Session getSession(String key) {
        return SESSIONS.get(key);
    }
}
