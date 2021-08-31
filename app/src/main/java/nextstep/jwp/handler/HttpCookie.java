package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, Cookie> cookieMap;

    public HttpCookie() {
        this.cookieMap = new HashMap<>();
    }

    public boolean contains(String name) {
        return cookieMap.containsKey(name);
    }

    public void setCookie(Cookie cookie) {
        cookieMap.put(cookie.getName(), cookie);
    }

    public Map<String, Cookie> getCookieMap() {
        return cookieMap;
    }
}
