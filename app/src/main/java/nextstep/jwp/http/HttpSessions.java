package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.http.entity.HttpSession;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String sessionId) {
        if (SESSIONS.containsKey(sessionId)) {
            return SESSIONS.get(sessionId);
        }
        return createSession();
    }

    public static HttpSession createSession() {
        String createdSessionId = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(createdSessionId);

        SESSIONS.put(createdSessionId, httpSession);

        return httpSession;
    }

    public static void add(String id, HttpSession session) {
        SESSIONS.put(id, session);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static void clear() {
        SESSIONS.clear();
    }
}
