package nextstep.jwp.http.common.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession get(String id) {
        if (HttpSessions.isValid(id)) {
            return SESSIONS.get(id);
        }
        return createSession();
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static HttpSession createSession() {
        String sessionId = createSessionId();
        return new HttpSession(sessionId);
    }

    private static String createSessionId() {
        return UUID.randomUUID().toString();
    }

    public static void putSession(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    public static boolean isValid(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }
}