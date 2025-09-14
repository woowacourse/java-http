package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // 세션값을 저장해 놓은 SESSIONS는 모든 요청 스레드에서 접근해 add, remove, find 로직을 호출 -> 동시성 문제가 발생할 수 있음
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public Session createSession() {
        final String sessionId = UUID.randomUUID().toString();
        final Session session = new Session(sessionId);
        add(session);
        return session;
    }
}

