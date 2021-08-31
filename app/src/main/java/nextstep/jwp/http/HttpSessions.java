package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static void put(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    private HttpSessions() {}
}