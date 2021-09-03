package nextstep.jwp.http.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap();

    private HttpSessions() {
    }

    public static HttpSession generate() {
        HttpSession httpSession = new HttpSession(UUID.randomUUID().toString());
        SESSIONS.put(httpSession.getId(), httpSession);
        return httpSession;
    }

    public static HttpSession getSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }
}
