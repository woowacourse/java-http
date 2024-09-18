package com.techcourse.session;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
        throw new IllegalStateException("this class can not be constructed.");
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public static boolean contains(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    public static boolean isValidJSessionId(String jSessionId) {
        return SESSIONS.values().stream().anyMatch(session -> session.getAttribute("JSESSIONID").equals(jSessionId));
    }

    public static void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
