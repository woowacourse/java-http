package com.techcourse.session;

import com.techcourse.exception.IllegalConstructionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ObjectUtils.Null;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
        throw new IllegalConstructionException(this.getClass());
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean isRegisteredId(String cookieId) {
        Session session = findSession(cookieId);
        return Objects.nonNull(session) && session.hasSameIdWith(cookieId);
    }

    public static Session findSession(String sessionId) {
        try {
            return SESSIONS.get(sessionId);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
