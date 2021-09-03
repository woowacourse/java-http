package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    private static final Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String id) {
        return sessions.keySet().stream()
                .filter(id::equals)
                .findAny()
                .map(sessions::get)
                .orElseThrow(IllegalArgumentException::new);
    }

    public static void remove(String id) {
        sessions.remove(id);
    }

    public static void add(String id, HttpSession httpSession) {
        sessions.put(id, httpSession);
    }

    public static HttpSession find(String sessionId) {
        return sessions.get(sessionId);
    }
}
