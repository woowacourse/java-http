package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static Session add(final String jsessionid) {
        final Session session = new Session(jsessionid);
        SESSIONS.put(jsessionid, session);
        return session;
    }

    public static Session findSession(final String jsessionid) {
        return SESSIONS.get(jsessionid);
    }
}
