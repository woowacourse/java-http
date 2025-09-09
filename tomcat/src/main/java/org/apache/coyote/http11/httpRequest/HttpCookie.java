package org.apache.coyote.http11.httpRequest;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Params cookies;

    private HttpCookie(final Params cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie parse(final String cookie) {
        return new HttpCookie(Params.parseFromCookie(cookie));
    }

    public static HttpCookie create(final String sessionId) {
        final Map<String, String> cookies = new HashMap<>();
        cookies.put("JSESSIONID", sessionId);
        return new HttpCookie(new Params(cookies));
    }

    public Map<String, String> getCookies() {
        return this.cookies.getParams();
    }
}
