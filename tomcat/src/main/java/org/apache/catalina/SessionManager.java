package org.apache.catalina;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public SessionManager() {
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.entrySet().stream()
                .filter(it -> it.getKey().equalsIgnoreCase(id))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("유효하지 않은 Session 아이디입니다."));
    }

    @Override
    public void remove(final Session session) {

    }

    public boolean hasSameSessionId(final String id) {
        return SESSIONS.entrySet().stream()
                .anyMatch(it -> it.getKey().equalsIgnoreCase(id));
    }
}
