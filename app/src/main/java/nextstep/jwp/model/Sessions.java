package nextstep.jwp.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sessions {

    private static final Map<String, Session> values = new ConcurrentHashMap<>();

    private Sessions() {
    }

    public static Session getSession(Cookie cookie) {
        String id = cookie.getSessionId();
        return values.computeIfAbsent(id, k -> new Session(id));
    }
}
