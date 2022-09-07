package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    public static final String COOKIES_DELIMITER = ": ";
    public static final String COOKIE_DELIMITER = "=";
    public static final int KEY = 0;
    public static final int VALUE = 1;

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public static HttpCookie request(final String message) {
        final Map<String, String> cookies = new HashMap<>();
        final String[] cookieElements = message.split(COOKIES_DELIMITER);
        for (final String cookie : cookieElements) {
            final String[] cookieElement = cookie.split(COOKIE_DELIMITER);
            cookies.put(cookieElement[KEY], cookieElement[VALUE]);
        }
        return new HttpCookie(cookies);
    }

    public static HttpCookie response(final String message) {
        final Map<String, String> cookies = new HashMap<>();
        cookies.put("JSESSIONID", message);
        return new HttpCookie(cookies);
    }

    public void generateSessionId() {
        if (cookies.containsKey("JSESSIONID")) {
            throw new IllegalArgumentException(String.format("Cookie 가 중복적으로 저장되었습니다. [%s]", "JSESSIONID"));
        }
        cookies.put("JSESSIONID", UUID.randomUUID().toString());
    }
}
