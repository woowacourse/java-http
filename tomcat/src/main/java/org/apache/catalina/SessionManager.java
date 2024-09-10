package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager implements Manager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
        log.info("세션 추가 성공! JSESSIONID: {}", session.getId());
    }

    @Override
    public Session findSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        throw new IllegalArgumentException("해당 세션 ID가 존재하지 않습니다.");
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {
    }

    private static class SingletonHolder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
