package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final Map<String, String> cookies = new HashMap<>();

    private HttpCookie() {}

    public static void save(final String cookie) {
        if (!cookies.containsKey(cookie)) {
            cookies.put(cookie, UUID.randomUUID().toString());
        }
    }

    public static void save(final String cookie, final String value) {
        cookies.put(cookie, value);
    }

    public static String ofJSessionId(final String sessionId) {
        return cookies.get(sessionId);
    }

    public static String cookieInfo(final String sessionId) {
        return sessionId + "=" + cookies.get(sessionId);
    }
}
