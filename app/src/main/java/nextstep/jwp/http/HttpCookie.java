package nextstep.jwp.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {

    private final Map<String, String> cookie;

    public HttpCookie() {
        this(new ConcurrentHashMap<>());
    }

    private HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public void setCookie(String key, String value) {
        cookie.put(key, value);
    }

    public Map<String, String> getCookie() {
        return cookie;
    }
}
