package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {
    public static final String SESSION_ID = "JSESSIONID";
    private final Map<String, HttpSession> sessionMap;

    public SessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(HttpSession session) {
        sessionMap.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        return sessionMap.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        sessionMap.remove(session.getId());
    }
}
