package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    // 각 키로 해시값 계산 -> 버킷(bucket, 내부 해시 테이블의 배열 요소 하나) 단위로 CAS 연산 이용해 락 걸기.
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }
}
