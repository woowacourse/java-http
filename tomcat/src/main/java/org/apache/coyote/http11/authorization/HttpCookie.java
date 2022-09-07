package org.apache.coyote.http11.authorization;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieStr) {
        final Map<String, String> httpCookie = new HashMap<>();

        if (cookieStr == null) {
            return new HttpCookie(httpCookie);
        }

        final String[] cookies = cookieStr.split("; ");

        for (String cookie : cookies) {
            final String[] keyValue = cookie.split("=");
            httpCookie.put(keyValue[0], keyValue[1]);
        }

        return new HttpCookie(httpCookie);
    }

    public String getCookieValue(final String key) {
        return cookies.get(key);
    }
}
