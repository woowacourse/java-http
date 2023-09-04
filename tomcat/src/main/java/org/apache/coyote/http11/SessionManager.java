package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.common.Session;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), (Session) session);
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
