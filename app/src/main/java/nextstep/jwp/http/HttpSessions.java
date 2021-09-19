package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        HttpSession httpSession = new HttpSession(id);
        SESSIONS.put(id, httpSession);
        return httpSession;
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }
}
