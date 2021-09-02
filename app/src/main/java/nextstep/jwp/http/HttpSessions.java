package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        HttpSession httpSession = SESSIONS.get(id);
        if (Objects.isNull(httpSession)) {
            String jSessionId = UUID.randomUUID().toString();
            httpSession = new HttpSession(jSessionId);
            put(httpSession);
        }
        return httpSession;
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static void put(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    private HttpSessions() {}
}