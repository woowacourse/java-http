package nextstep.jwp.request;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public static void addSession(String id){
        SESSIONS.put(id, new HttpSession(id));
    }

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    static void remove(String id) {
    }

    private HttpSessions() {
    }
}