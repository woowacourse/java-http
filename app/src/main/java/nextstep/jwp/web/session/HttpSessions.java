package nextstep.jwp.web.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {}

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }
}
