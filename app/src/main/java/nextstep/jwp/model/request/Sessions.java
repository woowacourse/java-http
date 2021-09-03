package nextstep.jwp.model.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sessions {

    private static final Map<String, Session> VALUES = new ConcurrentHashMap<>();

    private Sessions() {
    }

    public static Session getSession(Cookie cookie) {
        String id = cookie.getSessionId();
        return VALUES.computeIfAbsent(id, k -> new Session(id));
    }
}
