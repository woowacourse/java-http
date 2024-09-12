package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.List;
import org.apache.coyote.http11.exception.HttpFormatException;

public class HttpCookie {

    private static final String COOKIES_SEPARATOR = ";";
    private static final String COOKIE_SEPARATOR = "=";
    protected static final String SESSION_KEY = "JSESSIONID";

    private final String name;
    private final String value;

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static List<HttpCookie> createCookies(String cookies) {
        if (cookies == null || cookies.isBlank()) {
            return List.of();
        }
        return Arrays.stream(cookies.split(COOKIES_SEPARATOR))
                .map(HttpCookie::of)
                .toList();
    }

    private static HttpCookie of(String cookie) {
        if (!isValidCookie(cookie)) {
            throw new HttpFormatException("올바르지 않은 쿠키 형식입니다.");
        }
        int index = cookie.indexOf(COOKIE_SEPARATOR);
        String key = cookie.substring(0, index).trim();
        String value = cookie.substring(index + 1).trim();
        return new HttpCookie(key, value);
    }

    private static boolean isValidCookie(String cookie) {
        int index = cookie.indexOf(COOKIE_SEPARATOR);
        int cookieLastIndex = cookie.length() - 1;
        return index != -1 && index != cookieLastIndex;
    }

    public static HttpCookie ofSession(String sessionId) {
        return new HttpCookie(SESSION_KEY, sessionId);
    }

    public String toResponse() {
        return name + COOKIE_SEPARATOR + value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
