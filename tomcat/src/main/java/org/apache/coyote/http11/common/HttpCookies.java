package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    private final Map<String, String> cookies;

    public HttpCookies() {
        this(new HashMap<>());
    }

    private HttpCookies(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    public static HttpCookies from(String cookie) {
        return new HttpCookies(parseCookies(cookie));
    }

    private static Map<String, String> parseCookies(String cookiesString) {
        String[] cookieParts = cookiesString.split("; ");

        if (cookieParts.length == 0) {
            return new HashMap<>();
        }

        return Arrays.stream(cookieParts)
                .map(HttpCookies::getCookieKeyValue)
                .collect(Collectors.toMap(part -> part[0], part -> part[1]));
    }

    private static String[] getCookieKeyValue(String part) {
        String[] cookie = part.split("=");
        if (cookie.length != 2) {
            throw new IllegalArgumentException("잘못된 형식의 쿠키 문자열입니다.");
        }
        return cookie;
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    public void removeCookie(String key) {
        cookies.remove(key);
    }

    public String getCookieString() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    public boolean contains(String key) {
        return cookies.containsKey(key);
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
