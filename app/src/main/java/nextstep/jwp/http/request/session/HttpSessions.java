package nextstep.jwp.http.request.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private HttpSessions() { }

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public void remove(String id) {
        SESSIONS.remove(id);
    }
}
