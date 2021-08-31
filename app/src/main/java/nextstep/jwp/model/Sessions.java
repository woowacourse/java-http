package nextstep.jwp.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sessions {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private Sessions() {
    }

    public static Session getSession(String id) {
        if (!SESSIONS.containsKey(id)) {
            SESSIONS.put(id, new Session(id));
        }
        return SESSIONS.get(id);
    }
}
