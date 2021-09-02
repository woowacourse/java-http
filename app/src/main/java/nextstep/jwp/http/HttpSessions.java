package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static void addSession(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    public static HttpSession getSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        throw new IllegalArgumentException("세션이 존재하지 않습니다.");
    }

    public static void remove(String id) {
        try {
            HttpSession httpSession = SESSIONS.remove(id);
            httpSession.invalidate();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("검색sdf할 요소가 존재하지 않습니다.");
        }
    }
}
