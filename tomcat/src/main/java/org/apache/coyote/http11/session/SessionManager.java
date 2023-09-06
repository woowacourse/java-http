package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    // static!
    private static final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {
    }

    public static void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return sessions.get(id);
    }

    public static boolean isExist(final String id) {
        return sessions.containsKey(id);
    }

}
