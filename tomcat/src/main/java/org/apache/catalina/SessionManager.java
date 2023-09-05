package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.Session;

public class SessionManager {

    private static final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {}

    public static void add(final Session session) {
        sessions.put(UUID.randomUUID().toString(), session);
    }

    public static Session findSession(final String id) {
        return sessions.get(id);
    }

    public static void remove(final String id) {
        sessions.remove(id);
    }
}
