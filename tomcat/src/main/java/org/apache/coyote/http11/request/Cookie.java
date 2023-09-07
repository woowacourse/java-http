package org.apache.coyote.http11.request;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Cookie {

    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie create(String cookies) {
        Map<String, String> cookiesMap = new LinkedHashMap<>();
        for (String cookie : cookies.split(COOKIE_SEPARATOR)) {
            String[] splitCookie = cookie.split(COOKIE_DELIMITER);
            String cookieName = splitCookie[0];
            String cookieValue = splitCookie[1];
            cookiesMap.put(cookieName, cookieValue);
        }
        return new Cookie(cookiesMap);
    }

    public Optional<String> findCookieValueByCookieName(String cookieName) {
        if (cookies.containsKey(cookieName)) {
            return Optional.of(cookies.get(cookieName));
        }
        return Optional.empty();
    }
}
