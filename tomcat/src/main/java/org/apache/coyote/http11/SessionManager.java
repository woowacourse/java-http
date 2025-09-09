package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static Session getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public void add(HttpSession session) {
        
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        return null;
    }

    @Override
    public void remove(HttpSession session) {

    }
}
