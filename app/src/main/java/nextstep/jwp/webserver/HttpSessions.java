package nextstep.jwp.webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    public static final String JSESSIONID = "JSESSIONID";
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void setSession(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

    private HttpSessions() {
    }
}
