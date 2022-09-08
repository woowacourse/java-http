package org.apache.catalina;

import java.util.ArrayList;
import java.util.List;

public class SessionManager implements Manager {
    private static final List<Session> SESSIONS = new ArrayList<>();

    public SessionManager() {
    }

    @Override
    public void add(final Session session) {
        SESSIONS.add(session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.stream()
                .filter(it -> it.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("유효하지 않은 Session 아이디입니다."));
    }

    @Override
    public void remove(final Session session) {

    }

    public boolean hasSameSessionId(final String id) {
        return SESSIONS.stream()
                .anyMatch(it -> it.getId().equalsIgnoreCase(id));
    }
}
