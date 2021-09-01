package nextstep.jwp.web.http.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

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

    private static String createSessionId() {
        return UUID.randomUUID().toString();
    }

    public static HttpSession createSession() {
        String sessionId = createSessionId();
        return new HttpSession(sessionId);
    }

    public static void putSession(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    public static boolean isValid(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }
}
