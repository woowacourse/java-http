package com.techcourse.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> sessions;

    static {
        sessions = new HashMap<>();
    }

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (sessions == null) {
            throw new IllegalStateException("잘못된 세션 매니져 접근");
        }
        return new SessionManager();
    }


    @Override
    public void add(final HttpSession session) {
        sessions.put(UUID.randomUUID().toString(), session);
    }

    @Override
    public HttpSession findSession(final String id) {
        if (!sessions.containsKey(id)) {
            throw new IllegalArgumentException("존재하지 않는 세션");
        }

        return sessions.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        final var key = getSessionId(session);
        sessions.remove(key);
    }

    public String getSessionId(final HttpSession session) {
        return sessions.keySet()
                .stream()
                .filter(sessionKey -> sessions.get(sessionKey).getId().equals(session.getId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션"));
    }
}
