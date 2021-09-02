package nextstep.jwp.webserver.request;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final Map<String, Object> userSession = new HashMap<>();

    public void setAttribute(String key, Object value) {
        userSession.put(key, value);
    }

    public Object getAttribute(String key) {
        return userSession.get(key);
    }
}
