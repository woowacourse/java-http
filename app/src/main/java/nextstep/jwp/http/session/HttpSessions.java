package nextstep.jwp.http.session;

import java.util.Map;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {
    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();


    public static HttpSession getSession(String id) {
        if (id == null) {
            return null;
        }

        HttpSession session = SESSIONS.get(id);
        if (session == null) {
            session = new HttpSession(id);
            SESSIONS.put(id, session);
            return session;
        }

        return session;
    }

    static void remove(String id) {
        SESSIONS.remove(id);
    }

    private HttpSessions() {}
}
