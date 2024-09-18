package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Session add(String key, Object object) {
        Session session = new Session();
        session.setAttribute(key, object);
        sessions.put(session.getId(), session);
        return session;
    }

    public static boolean contains(String session) {
        return sessions.containsKey(session);
    }
}
