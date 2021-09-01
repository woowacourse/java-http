package nextstep.jwp.framework.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {}

    public static HttpSession getSessionOrDefault(String id, HttpSession httpSession) {
        return SESSIONS.getOrDefault(id, httpSession);
    }

    public static void putIfAbsent(String id, HttpSession httpSession) {
        SESSIONS.putIfAbsent(id, httpSession);
    }
}
