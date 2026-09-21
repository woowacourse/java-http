package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(String id) {
        return SESSIONS.get(id);
    }

    // key가 맵에 있으면 값을 만들고 반환
    // key가 없으면 호출하지 않고 기존 값 반환
    public static Session getOrCreate(String id) {
        return SESSIONS.computeIfAbsent(id, Session::new);
    }

    public static void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {}
}
