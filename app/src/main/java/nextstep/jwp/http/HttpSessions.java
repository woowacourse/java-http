package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    static void add(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

    private HttpSessions() {}
}