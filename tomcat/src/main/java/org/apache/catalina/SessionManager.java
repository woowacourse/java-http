package org.apache.catalina;

import org.apache.coyote.http11.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    public static void remove(Session session) {

    }
}
