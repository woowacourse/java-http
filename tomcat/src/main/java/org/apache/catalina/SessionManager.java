package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 상속이 이미 private로 막혀있지만 명시적으로 final 클래스 임을 선언
public final class SessionManager implements Manager {

    // static!
    // key: JSESSIONID, value: Session
    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    // 싱글톤 패턴으로 생성자는 private으로 막아두고, getInstance를 통해 이미 생성된 SessionManager를 사용
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        Session session = SESSIONS.get(id);
        if (session == null) {
            throw new IllegalArgumentException("해당 uid의 세션이 없음");
        }

        return session;
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean isExistSession(final String id) {
        return SESSIONS.containsKey(id);
    }
}
