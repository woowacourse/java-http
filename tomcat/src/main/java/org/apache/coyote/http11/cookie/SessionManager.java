package org.apache.coyote.http11.cookie;

import org.apache.coyote.http11.exception.NotFoundSessionException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        throw new NotFoundSessionException();
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }
}
