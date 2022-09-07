package session;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private Map<String, Cookie> cookies = new ConcurrentHashMap<>();

    public void addCookie(final String key, final String value) {
        cookies.put(key, new Cookie(key, value));
    }

    public Map<String, Cookie> getCookies() {
        return Collections.unmodifiableMap(cookies);
    }
}
