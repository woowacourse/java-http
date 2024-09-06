package com.techcourse;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(String id) {
        if (!SESSIONS.containsKey(id)) {
            try {
                throw new IOException("no such session id");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return SESSIONS.get(id);
    }
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
