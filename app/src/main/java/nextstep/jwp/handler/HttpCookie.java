package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class HttpCookie {
    private final Map<String, Cookie> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public boolean contains(String name) {
        return cookies.containsKey(name);
    }

    public Cookie getCookie(String name) {
        if (cookies.containsKey(name)) {
            return cookies.get(name);
        }

        throw new NoSuchElementException("[" + name + "]을/를 name으로 가지는 Cookie가 존재하지 않습니다.");
    }

    public void setCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public Map<String, Cookie> getCookies() {
        return cookies;
    }
}
