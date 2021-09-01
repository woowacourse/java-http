package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSessions {

    private static final Logger log = LoggerFactory.getLogger(HttpSessions.class);

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static HttpSession getSession(String id) {
        HttpSession httpSession = SESSIONS.get(id);
        if (httpSession == null) {
            log.debug("세션이 없습니다 = {}" , "null");
            return new HttpSession(id);
        }
        log.debug("세션이 있습니다 ok");
        return httpSession;
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }

    private HttpSessions() {

    }

    public static void setAttribute(String id, HttpSession httpSession) {
        SESSIONS.put(id, httpSession);
    }
}
