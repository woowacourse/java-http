package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.Session;

public class SessionManager {

    private static final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {
    }

    public static void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }

    public static void remove(final String id) {
        sessions.remove(id);
    }
}
