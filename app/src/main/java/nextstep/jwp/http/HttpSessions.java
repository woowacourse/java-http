package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {}

    public static HttpSession getSession(String id) {
        if(SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }

        throw new IllegalArgumentException("해당 세션 ID가 존재하지 않습니다.");
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static HttpSession createSession() {
        String sessionId = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(sessionId);
        SESSIONS.put(sessionId, httpSession);

        return httpSession;
    }
}
