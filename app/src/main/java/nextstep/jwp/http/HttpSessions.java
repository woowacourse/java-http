package nextstep.jwp.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {

    private static final Map<String, HttpSession> STORAGE = new ConcurrentHashMap<>();

    private HttpSessions() {
    }

    public static void saveSession(HttpSession httpSession) {
        STORAGE.put(httpSession.getId(), httpSession);
    }

    public static HttpSession getSession(String id) {
        return STORAGE.get(id);
    }

    public static void removeSession(String id) {
        STORAGE.remove(id);
    }
}
