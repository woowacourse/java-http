package nextstep.jwp.http.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private HttpSessions() {}

    public static HttpSession createSession() {
        final String id = UUID.randomUUID().toString();
        final HttpSession httpSession = new HttpSession(id);
        SESSIONS.put(id, httpSession);
        return httpSession;
    }

    public static Optional<HttpSession> getSession(String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }
}
