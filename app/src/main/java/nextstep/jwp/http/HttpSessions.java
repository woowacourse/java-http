package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSessions {

    private static final Logger log = LoggerFactory.getLogger(HttpSessions.class);

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private HttpSessions() {

    }

    public static HttpSession getSession(String id) {
        HttpSession httpSession = SESSIONS.get(id);
        if (Objects.isNull(httpSession)) {
            log.debug("session 을 등록합니다.");
            httpSession = new HttpSession(id);
            setAttribute(id, httpSession);
        }
        return httpSession;
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    public static void setAttribute(String id, HttpSession httpSession) {
        SESSIONS.put(id, httpSession);
    }
}
