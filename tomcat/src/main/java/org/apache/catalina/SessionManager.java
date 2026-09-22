package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

// 모든 클라이언트의 세션 값을 관리하는 클래스
// 각 세션의 id 는 uuid 사용
public class SessionManager implements Manager{

    // key = JSESSION 아이디값,value = Session
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        String id = session.getId();
        SESSIONS.put(id, session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public SessionManager() {}
}
