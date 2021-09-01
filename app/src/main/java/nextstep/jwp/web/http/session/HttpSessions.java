package nextstep.jwp.web.http.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        if (SESSIONS.containsKey(id)) {
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
        HttpSession httpSession = new HttpSession(sessionId);
        SESSIONS.put(sessionId, httpSession);
        return httpSession;
    }
}
