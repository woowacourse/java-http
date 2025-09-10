package org.apache.coyote.http11.session;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(HttpSession session) {
        UUID uuid = UUID.randomUUID();
        SESSIONS.put(uuid.toString(), (Session) session);
    }

    @Override
    public HttpSession findSession(String id) {
        return (HttpSession) SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
