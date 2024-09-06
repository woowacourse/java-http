package org.apache.catalina.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.apache.catalina.Manager;
import org.apache.coyote.http11.Session;

public class SessionManager implements Manager {
    private static final SessionManager instance = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(Session session) {

    }

    @Override
    public Session findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
    }
}
