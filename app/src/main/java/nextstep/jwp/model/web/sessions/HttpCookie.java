package nextstep.jwp.model.web.sessions;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies = new HashMap<>();

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public String getSessionValue() {
        return cookies.get("JSESSIONID");
    }
}
