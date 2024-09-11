package com.techcourse.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }

    public static boolean isRegisitered(final String id) {
        return SESSIONS.containsKey(id);
    }
}
