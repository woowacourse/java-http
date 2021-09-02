package nextstep.jwp.httpmessage;

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
        return SESSIONS.get(id);
    }
}
