package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, Session> sessions = new LinkedHashMap<>();

    public void addSession(String id, Session session) {
        sessions.put(id, session);
    }

    public Session getOrCreateSession(String id) {
        if(sessions.containsKey(id)) {
            return sessions.get(id);
        }
        sessions.put(id, new Session(id, new LinkedHashMap<>()));
        return sessions.get(id);
    }

    public boolean containsSession(String id) {
        return sessions.containsKey(id);
    }

    public boolean isSessionContainsKey(String id, String key) {
        if(!sessions.containsKey(id)) {
            return false;
        }
        return sessions.get(id).getUser(key) != null;
    }

    public boolean containsSessionKey(String key) {
        return sessions.values().stream().anyMatch(session -> session.getUser(key) != null);
    }
}
