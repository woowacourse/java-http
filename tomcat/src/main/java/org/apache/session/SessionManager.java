package org.apache.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.Cookies;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session generateNewSession() {
        UUID uuid = UUID.randomUUID();
        return new Session(uuid.toString());
    }

    public static boolean isValidSession(Cookies cookies) {
        return cookies.findJSessionId()
            .filter(SessionManager::contains)
            .isPresent();
    }

    public static boolean contains(String id) {
        return SESSIONS.containsKey(id);
    }
}
