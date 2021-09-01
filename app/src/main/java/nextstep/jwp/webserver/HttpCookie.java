package nextstep.jwp.webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public HttpCookie() {
        this(new HashMap<>());
    }

    public HttpCookie(String cookieString) {
        this(parseCookie(cookieString));
    }

    private static Map<String, String> parseCookie(String cookieString) {
        Map<String, String> cookie = new HashMap<>();
        for (String queryString : cookieString.split(";")) {
            putCookie(cookie, queryString.trim());
        }
        return cookie;
    }

    private static void putCookie(Map<String, String> cookie, String queryString) {
        int index = queryString.indexOf("=");
        String key = queryString.substring(0, index);
        String value = queryString.substring(index + 1).trim();
        cookie.put(key, value);
    }

    public String get(String name) {
        return cookie.get(name);
    }
}
