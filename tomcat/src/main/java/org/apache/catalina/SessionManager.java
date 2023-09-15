package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Session addSession(final Session session) {
        SESSIONS.put(session.getId(), session);
        return session;
    }

    public static Session findSession(final String id){
        if(!SESSIONS.containsKey(id)) {
            return null;
        }
        return SESSIONS.get(id);
    }

    public static void removeSession(final String id) {
        SESSIONS.remove(id);
    }
}
