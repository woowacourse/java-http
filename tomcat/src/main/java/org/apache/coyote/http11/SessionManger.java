package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class SessionManger {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManger() {

    }

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}
