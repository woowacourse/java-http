package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        HttpSession httpSession = SESSIONS.get(id);

        if (httpSession == null) {
            httpSession = new HttpSession(id);
            SESSIONS.put(id, httpSession);
        }
        return httpSession;
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }
}
