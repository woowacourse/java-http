package org.apache.catalina.servlet.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static void add(Session session) {
        sessions.put(session.id(), session);
    }

    public static Session findSession(String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }

    public static void remove(String id) {
        Session remove = sessions.remove(id);
        remove.invalidate();
    }
}
