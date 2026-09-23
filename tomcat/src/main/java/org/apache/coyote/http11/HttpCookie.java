package org.apache.coyote.http11;

import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies;

    HttpCookie(String cookieHeader) {
        cookies = HttpRequestUtils.parseCookieHeader(cookieHeader);
    }

    public void add(String key, String value) {
        cookies.put(key, value);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
