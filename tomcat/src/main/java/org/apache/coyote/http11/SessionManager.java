package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.common.Session;

public class SessionManager implements Manager {

    public static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), (Session) session);
    }

    @Override
    public HttpSession findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    public boolean contains(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }
}
