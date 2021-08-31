package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;

public class Sessions {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private Sessions() {
    }

    public static Session getSession(String id) {
        if (!SESSIONS.containsKey(id)) {
            SESSIONS.put(id, new Session(id));
        }
        return SESSIONS.get(id);
    }
}
