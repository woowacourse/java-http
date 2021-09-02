package nextstep.jwp.framework.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpSessions {

    private static final String SESSION_ID = "JSESSIONID";

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static boolean isLoggedIn(final String id) {
        Map<String, HttpSession> sessions = SESSIONS;
        final HttpSession httpSession = SESSIONS.get(id);

        return Objects.nonNull(httpSession);
    }

    public static void putSession(final HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    public static HttpSession getSession(final String id) {
        return new HttpSession(id);
    }
}
