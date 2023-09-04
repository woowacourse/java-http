package org.apache.coyote.http11.cookie;

import org.apache.coyote.http11.exception.NotFoundSessionException;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

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
