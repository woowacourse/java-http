package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 모든 클라이언트의 세션 값을 관리하는 클래스
public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

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

    public static HttpSession createSession() {
        String sessionId = UUID.randomUUID().toString();
        HttpSession session = new HttpSession(sessionId);
        SESSIONS.put(sessionId, session);
        return session;
    }
}
