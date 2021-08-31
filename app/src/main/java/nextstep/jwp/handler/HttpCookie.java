package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class HttpCookie {
    private final Map<String, Cookie> cookieMap;

    public HttpCookie() {
        this.cookieMap = new HashMap<>();
    }

    public boolean contains(String name) {
        return cookieMap.containsKey(name);
    }

    public Cookie getCookie(String name) {
        if (cookieMap.containsKey(name)) {
            return cookieMap.get(name);
        }
        throw new NoSuchElementException("쿠키에 Session이 존재하지 않습니다.");
    }

    public void setCookie(Cookie cookie) {
        cookieMap.put(cookie.getName(), cookie);
    }

    public Map<String, Cookie> getCookieMap() {
        return cookieMap;
    }
}
