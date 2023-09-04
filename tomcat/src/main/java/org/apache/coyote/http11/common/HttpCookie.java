package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIES_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        Arrays.stream(cookieHeader.split(COOKIES_DELIMITER))
                .map(cookie -> cookie.split(COOKIE_DELIMITER))
                .forEach(cookie -> cookies.put(cookie[0], cookie[1]));
        return new HttpCookie(cookies);
    }

    public String getCookieValue(String name) {
        return cookies.getOrDefault(name, null);
    }
}
