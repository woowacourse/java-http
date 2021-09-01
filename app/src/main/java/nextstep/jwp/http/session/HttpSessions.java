package nextstep.jwp.http.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void putSession(String id) {
        SESSIONS.put(id, new HttpSession(id));
    }

    private HttpSessions() {}
}
