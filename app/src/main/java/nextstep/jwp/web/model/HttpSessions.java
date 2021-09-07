package nextstep.jwp.web.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static Optional<HttpSession> getSession(String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public static void removeSession(String id) {
        SESSIONS.remove(id);
    }

    public static HttpSession save(HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
        return httpSession;
    }

    public static boolean isExistsId(String jSessionId) {
        return SESSIONS.containsKey(jSessionId);
    }
}
