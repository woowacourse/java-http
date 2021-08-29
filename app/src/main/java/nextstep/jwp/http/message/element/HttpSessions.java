package nextstep.jwp.http.message.element;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpSessions {

    public static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    public static Optional<HttpSession> get(String uuid) {
        return Optional.ofNullable(SESSIONS.get(uuid));
    }

    public static void remove(String uuid) {
        SESSIONS.remove(uuid);
    }

    public static void put(HttpSession httpSession) {
        SESSIONS.put(httpSession.getSessionId(), httpSession);
    }
}
