package nextstep.jwp.model;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void setCookie(final String key, final String value) {
        cookies.put(key, value);
    }

    public String getCookie(final String key) {
        return cookies.get(key);
    }
}
